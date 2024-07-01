package org.camunda.community.migration.converter;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
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
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.camunda.bpm.model.xml.impl.util.ModelIoException;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnConverter.MustacheContext.MustacheResultContext;
import org.camunda.community.migration.converter.BpmnConverter.MustacheContext.MustacheResultContext.MustacheElementResultContext;
import org.camunda.community.migration.converter.BpmnConverter.MustacheContext.MustacheResultContext.MustacheElementResultContext.MustacheSeverityContext;
import org.camunda.community.migration.converter.BpmnConverter.MustacheContext.MustacheResultContext.MustacheElementResultContext.MustacheSeverityContext.MustacheMessageContext;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.camunda.community.migration.converter.DomElementVisitorContext.DefaultDomElementVisitorContext;
import org.camunda.community.migration.converter.conversion.Conversion;
import org.camunda.community.migration.converter.visitor.AbstractProcessElementVisitor;
import org.camunda.community.migration.converter.visitor.DomElementVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpmnConverter {
  private static final Logger LOG = LoggerFactory.getLogger(BpmnConverter.class);
  private static final Template MARKDOWN_TEMPLATE;

  static {
    try (InputStream in =
        BpmnConverter.class
            .getClassLoader()
            .getResourceAsStream("bpmn-diagram-check-result.mustache")) {
      MARKDOWN_TEMPLATE = Mustache.compiler().compile(new String(in.readAllBytes()));
    } catch (IOException e) {
      throw new RuntimeException("Error while loading result printer template", e);
    }
  }

  private final BpmnParser bpmnParser = new BpmnParser();
  private final List<DomElementVisitor> visitors;
  private final List<Conversion> conversions;
  private final NotificationService notificationService;

  public BpmnConverter(
      List<DomElementVisitor> visitors,
      List<Conversion> conversions,
      NotificationService notificationService) {
    this.visitors = visitors;
    this.conversions = conversions;
    this.notificationService = notificationService;
  }

  public void convert(BpmnModelInstance modelInstance, ConverterProperties properties) {
    check(null, modelInstance, properties);
  }

  public BpmnDiagramCheckResult check(
      String filename, BpmnModelInstance modelInstance, ConverterProperties properties) {
    LOG.info("Start check");
    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
    result.setFilename(filename);
    result.setConverterVersion(getClass().getPackage().getImplementationVersion());
    BpmnDiagramCheckContext context = new BpmnDiagramCheckContext();
    traverse(modelInstance.getDocument().getRootElement(), result, context, properties);
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
              List<BpmnElementCheckMessage> messages = getMessages(element, result);
              List<String> references = getReferences(element, result);
              List<String> referencedBys = getReferencedBys(element, result);
              conversionElementAppender.appendMessages(element, messages);
              conversionElementAppender.appendReferences(element, references);
              conversionElementAppender.appendReferencedBy(element, referencedBys);
              if (properties.getAppendDocumentation()) {
                conversionElementAppender.appendDocumentation(
                    element, collectMessages(result, messages, references));
              }
              conversions.forEach(conversion -> conversion.convert(element, convertible, messages));
            });
    LOG.info("Done with conversion");
    return result;
  }

  private List<BpmnElementCheckMessage> collectMessages(
      BpmnDiagramCheckResult result,
      List<BpmnElementCheckMessage> messages,
      List<String> references) {
    List<BpmnElementCheckMessage> collectedMessages = new ArrayList<>();
    collectedMessages.addAll(messages);
    collectedMessages.addAll(
        references.stream()
            .flatMap(reference -> getMessages(reference, result).stream())
            .collect(Collectors.toList()));
    return collectedMessages;
  }

  private List<BpmnElementCheckMessage> getMessages(
      DomElement element, BpmnDiagramCheckResult result) {
    return result.getResults().stream()
        .filter(
            elementCheckResult ->
                elementCheckResult.getElementId().equals(element.getAttribute("id")))
        .map(BpmnElementCheckResult::getMessages)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private List<BpmnElementCheckMessage> getMessages(
      String elementId, BpmnDiagramCheckResult result) {
    return result.getResults().stream()
        .filter(elementCheckResult -> elementCheckResult.getElementId().equals(elementId))
        .map(BpmnElementCheckResult::getMessages)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private List<String> getReferences(DomElement element, BpmnDiagramCheckResult result) {
    return result.getResults().stream()
        .filter(
            elementCheckResult ->
                elementCheckResult.getElementId().equals(element.getAttribute("id")))
        .map(BpmnElementCheckResult::getReferences)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  private List<String> getReferencedBys(DomElement element, BpmnDiagramCheckResult result) {
    return result.getResults().stream()
        .filter(
            elementCheckResult ->
                elementCheckResult.getElementId().equals(element.getAttribute("id")))
        .map(BpmnElementCheckResult::getReferencedBy)
        .findFirst()
        .orElseGet(ArrayList::new);
  }

  public void printXml(DomDocument document, boolean prettyPrint, Writer writer) {
    bpmnParser.validateModel(document);
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
      BpmnDiagramCheckResult result,
      BpmnDiagramCheckContext context,
      ConverterProperties properties) {
    DomElementVisitorContext elementContext =
        new DefaultDomElementVisitorContext(
            element, context, result, notificationService, properties);
    visitors.stream()
        .sorted(Comparator.comparingInt(v -> v instanceof AbstractProcessElementVisitor ? 2 : 3))
        .forEach(visitor -> visitor.visit(elementContext));
    element.getChildElements().forEach(child -> traverse(child, result, context, properties));
  }

  public void writeCsvFile(List<BpmnDiagramCheckResult> results, Writer writer) {
    try (ICSVWriter csvWriter = new CSVWriterBuilder(writer).withSeparator(';').build()) {
      csvWriter.writeNext(createHeaders());
      csvWriter.writeAll(createLines(results));
    } catch (IOException e) {
      throw new RuntimeException("Error while writing csv file", e);
    }
  }

  public void writeMarkdownFile(List<BpmnDiagramCheckResult> results, Writer writer) {
    MARKDOWN_TEMPLATE.execute(createContext(results), writer);
  }

  private MustacheContext createContext(List<BpmnDiagramCheckResult> results) {
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
      "filename", "elementName", "elementId", "elementType", "severity", "message", "link"
    };
  }

  private List<String[]> createLines(List<BpmnDiagramCheckResult> results) {
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
