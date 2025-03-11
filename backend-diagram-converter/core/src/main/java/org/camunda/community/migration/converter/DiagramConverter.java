package org.camunda.community.migration.converter;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.camunda.bpm.model.dmn.impl.DmnParser;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.impl.parser.AbstractModelParser;
import org.camunda.bpm.model.xml.impl.util.ModelIoException;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckResult;
import org.camunda.community.migration.converter.DiagramConverter.MustacheContext.MustacheResultContext;
import org.camunda.community.migration.converter.DiagramConverter.MustacheContext.MustacheResultContext.MustacheElementResultContext;
import org.camunda.community.migration.converter.DiagramConverter.MustacheContext.MustacheResultContext.MustacheElementResultContext.MustacheSeverityContext;
import org.camunda.community.migration.converter.DiagramConverter.MustacheContext.MustacheResultContext.MustacheElementResultContext.MustacheSeverityContext.MustacheMessageContext;
import org.camunda.community.migration.converter.DomElementVisitorContext.DefaultDomElementVisitorContext;
import org.camunda.community.migration.converter.conversion.Conversion;
import org.camunda.community.migration.converter.visitor.AbstractDmnElementVisitor;
import org.camunda.community.migration.converter.visitor.AbstractProcessElementVisitor;
import org.camunda.community.migration.converter.visitor.DomElementVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagramConverter {
  private static final Logger LOG = LoggerFactory.getLogger(DiagramConverter.class);
  private static final Template MARKDOWN_TEMPLATE;

  static {
    try (InputStream in =
        DiagramConverter.class
            .getClassLoader()
            .getResourceAsStream("bpmn-diagram-check-result.mustache")) {
      MARKDOWN_TEMPLATE = Mustache.compiler().compile(new String(in.readAllBytes()));
    } catch (IOException e) {
      throw new RuntimeException("Error while loading result printer template", e);
    }
  }

  private final List<DomElementVisitor> visitors;
  private final List<Conversion> conversions;
  private final NotificationService notificationService;

  public DiagramConverter(
      List<DomElementVisitor> visitors,
      List<Conversion> conversions,
      NotificationService notificationService) {
    this.visitors = visitors;
    this.conversions = conversions;
    this.notificationService = notificationService;
  }

  public void convert(ModelInstance modelInstance, ConverterProperties properties) {
    check(null, modelInstance, properties);
  }

  public DiagramCheckResult check(
      String filename, ModelInstance modelInstance, ConverterProperties converterProperties) {
    return check(filename, modelInstance.getDocument().getRootElement(), converterProperties);
  }

  private DiagramCheckResult check(
      String filename, DomElement rootElement, ConverterProperties properties) {
    LOG.info("Start check");
    DiagramCheckResult result = new DiagramCheckResult();
    result.setFilename(filename);
    result.setConverterVersion(getClass().getPackage().getImplementationVersion());
    DiagramCheckContext context = new DiagramCheckContext();
    traverse(rootElement, result, context, properties);
    LOG.info("Done check");
    LOG.info("Start remove of old elements");
    context
        .getElementsToRemove()
        .forEach(element -> element.getParentElement().removeChild(element));
    context
        .getAttributesToRemove()
        .forEach(
            (element, stringSetMap) ->
                stringSetMap.forEach(
                    (namespaceUri, attributes) ->
                        attributes.forEach(
                            attribute -> element.removeAttribute(namespaceUri, attribute))));
    LOG.info("Done remove of old elements");
    LOG.info("Start conversion");
    ConversionElementAppender conversionElementAppender = new ConversionElementAppender();
    context
        .getConvertibles()
        .forEach(
            (element, convertible) -> {
              List<ElementCheckMessage> messages = getMessages(element, result);
              List<String> references = getReferences(element, result);
              List<String> referencedBys = getReferencedBys(element, result);
              if (properties.getAppendElements()) {
                conversionElementAppender.appendMessages(element, messages);
                conversionElementAppender.appendReferences(element, references);
                conversionElementAppender.appendReferencedBy(element, referencedBys);
              }
              if (properties.getAppendDocumentation() && isBpmn(rootElement.getDocument())) {
                conversionElementAppender.appendDocumentation(
                    element, collectMessages(result, messages, references));
              }
              conversions.forEach(conversion -> conversion.convert(element, convertible, messages));
            });
    LOG.info("Done with conversion");
    return result;
  }

  private List<ElementCheckMessage> collectMessages(
      DiagramCheckResult result, List<ElementCheckMessage> messages, List<String> references) {
    List<ElementCheckMessage> collectedMessages = new ArrayList<>();
    collectedMessages.addAll(messages);
    collectedMessages.addAll(
        references.stream()
            .flatMap(reference -> getMessages(reference, result).stream())
            .collect(Collectors.toList()));
    return collectedMessages;
  }

  private List<ElementCheckMessage> getMessages(DomElement element, DiagramCheckResult result) {
    return result.getResults().stream()
        .filter(
            elementCheckResult ->
                elementCheckResult.getElementId().equals(element.getAttribute("id")))
        .map(ElementCheckResult::getMessages)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private List<ElementCheckMessage> getMessages(String elementId, DiagramCheckResult result) {
    return result.getResults().stream()
        .filter(elementCheckResult -> elementCheckResult.getElementId().equals(elementId))
        .map(ElementCheckResult::getMessages)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private List<String> getReferences(DomElement element, DiagramCheckResult result) {
    return result.getResults().stream()
        .filter(
            elementCheckResult ->
                elementCheckResult.getElementId().equals(element.getAttribute("id")))
        .map(ElementCheckResult::getReferences)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private List<String> getReferencedBys(DomElement element, DiagramCheckResult result) {
    return result.getResults().stream()
        .filter(
            elementCheckResult ->
                elementCheckResult.getElementId().equals(element.getAttribute("id")))
        .map(ElementCheckResult::getReferencedBy)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private boolean isBpmn(DomDocument document) {
    return determineDiagramType(document) == DiagramType.BPMN;
  }

  private DiagramType determineDiagramType(DomDocument document) {
    if (document.getRootElement().getNamespaceURI().equals(BPMN)) {
      return DiagramType.BPMN;
    }
    if (Arrays.asList(DMN).contains(document.getRootElement().getNamespaceURI())) {
      return DiagramType.DMN;
    }
    throw new IllegalArgumentException(
        "Unknown document namespace: " + document.getRootElement().getNamespaceURI());
  }

  private AbstractModelParser getParser(DomDocument document) {
    return switch (determineDiagramType(document)) {
      case DMN -> new DmnParser();
      case BPMN -> new BpmnParser();
    };
  }

  public void printXml(DomDocument document, boolean prettyPrint, Writer writer) {
    getParser(document).validateModel(document);
    StreamResult result = new StreamResult(writer);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream("prettyprint.xsl")) {
      Transformer transformer =
          transformerFactory.newTransformer(new StreamSource(new InputStreamReader(in)));
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      if (prettyPrint) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      }
      synchronized (document) {
        transformer.transform(document.getDomSource(), result);
      }
    } catch (TransformerConfigurationException e) {
      throw new ModelIoException("Unable to create a transformer for the model", e);
    } catch (TransformerException e) {
      throw new ModelIoException("Unable to transform model to xml", e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void traverse(
      DomElement element,
      DiagramCheckResult result,
      DiagramCheckContext context,
      ConverterProperties properties) {
    DomElementVisitorContext elementContext =
        new DefaultDomElementVisitorContext(
            element, context, result, notificationService, properties);
    visitors.stream()
        .sorted(Comparator.comparingInt(this::sortVisitor))
        .forEach(visitor -> visitor.visit(elementContext));
    element.getChildElements().forEach(child -> traverse(child, result, context, properties));
  }

  private int sortVisitor(DomElementVisitor visitor) {
    if (visitor instanceof AbstractProcessElementVisitor) {
      // apply process element visitors first
      return 2;
    }
    if (visitor instanceof AbstractDmnElementVisitor) {
      // apply dmn element visitors first
      return 2;
    }
    if (visitor
        .getClass()
        .getPackageName()
        .startsWith(DomElementVisitor.class.getPackage().getName())) {
      // then, apply native implementations
      return 3;
    }
    // everything else is applied last
    return 4;
  }

  public void writeCsvFile(List<DiagramCheckResult> results, Writer writer) {
    try (ICSVWriter csvWriter = new CSVWriterBuilder(writer).withSeparator(';').build()) {
      csvWriter.writeNext(createHeaders());
      csvWriter.writeAll(createLines(results));
    } catch (IOException e) {
      throw new RuntimeException("Error while writing csv file", e);
    }
  }

  public void writeMarkdownFile(List<DiagramCheckResult> results, Writer writer) {
    MARKDOWN_TEMPLATE.execute(createContext(results), writer);
  }

  private MustacheContext createContext(List<DiagramCheckResult> results) {
    List<MustacheResultContext> contexts = new ArrayList<>();
    results.forEach(
        bpmnDiagramCheckResult -> {
          List<MustacheElementResultContext> resultList = new ArrayList<>();
          bpmnDiagramCheckResult
              .getResults()
              .forEach(
                  bpmnElementCheckResult -> {
                    List<MustacheSeverityContext> severities = new ArrayList<>();
                    bpmnElementCheckResult
                        .getMessages()
                        .forEach(
                            bpmnElementCheckMessage -> {
                              // create severity if absent
                              MustacheSeverityContext mustacheSeverityContext =
                                  severities.stream()
                                      .filter(
                                          severity ->
                                              severity
                                                  .severity()
                                                  .equals(
                                                      bpmnElementCheckMessage.getSeverity().name()))
                                      .findFirst()
                                      .orElseGet(
                                          () -> {
                                            List<MustacheMessageContext> messages =
                                                new ArrayList<>();
                                            MustacheSeverityContext newMustacheSeverityContext =
                                                new MustacheSeverityContext(
                                                    bpmnElementCheckMessage.getSeverity().name(),
                                                    messages,
                                                    (frag, out) ->
                                                        out.write(String.valueOf(messages.size())));
                                            severities.add(newMustacheSeverityContext);
                                            return newMustacheSeverityContext;
                                          });
                              mustacheSeverityContext
                                  .messages()
                                  .add(
                                      new MustacheMessageContext(
                                          bpmnElementCheckMessage.getMessage(),
                                          bpmnElementCheckMessage.getLink()));
                            });
                    resultList.add(
                        new MustacheElementResultContext(
                            bpmnElementCheckResult.getElementName(),
                            bpmnElementCheckResult.getElementId(),
                            bpmnElementCheckResult.getElementType(),
                            severities));
                  });
          contexts.add(new MustacheResultContext(bpmnDiagramCheckResult.getFilename(), resultList));
        });
    return new MustacheContext(contexts);
  }

  private String[] createHeaders() {
    return new String[] {
      "filename",
      "elementName",
      "elementId",
      "elementType",
      "severity",
      "messageId",
      "message",
      "link"
    };
  }

  private List<String[]> createLines(List<DiagramCheckResult> results) {
    return results.stream()
        .flatMap(
            diagramCheckResult ->
                diagramCheckResult.getResults().stream()
                    .flatMap(
                        elementCheckResult ->
                            elementCheckResult.getMessages().stream()
                                .map(
                                    message ->
                                        new String[] {
                                          diagramCheckResult.getFilename(),
                                          elementCheckResult.getElementName(),
                                          elementCheckResult.getElementId(),
                                          elementCheckResult.getElementType(),
                                          message.getSeverity().name(),
                                          message.getId(),
                                          message.getMessage(),
                                          message.getLink()
                                        })))
        .collect(Collectors.toList());
  }

  record MustacheContext(List<MustacheResultContext> contexts) {
    record MustacheResultContext(String filename, List<MustacheElementResultContext> results) {
      record MustacheElementResultContext(
          String elementName,
          String elementId,
          String elementType,
          List<MustacheSeverityContext> severities) {
        record MustacheSeverityContext(
            String severity, List<MustacheMessageContext> messages, Mustache.Lambda count) {
          record MustacheMessageContext(String message, String link) {}
        }
      }
    }
  }
}
