package org.camunda.community.migration.converter;

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
import org.camunda.bpm.model.xml.impl.util.ModelIoException;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
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

  public void convert(
      BpmnModelInstance modelInstance,
      boolean appendDocumentation,
      ConverterProperties properties) {
    check(null, modelInstance, appendDocumentation, properties);
  }

  public BpmnDiagramCheckResult check(
      String filename,
      BpmnModelInstance modelInstance,
      boolean appendDocumentation,
      ConverterProperties properties) {
    LOG.info("Start check");
    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
    result.setFilename(filename);
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
              if (appendDocumentation) {
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
}
