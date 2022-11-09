package org.camunda.community.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckResult;
import org.camunda.community.converter.DomElementVisitorContext.DefaultDomElementVisitorContext;
import org.camunda.community.converter.conversion.Conversion;
import org.camunda.community.converter.visitor.AbstractProcessElementVisitor;
import org.camunda.community.converter.visitor.DomElementVisitor;
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

  public void convert(BpmnModelInstance modelInstance, boolean appendDocumentation) {
    check(null, modelInstance, appendDocumentation);
  }

  public BpmnDiagramCheckResult check(
      String filename, BpmnModelInstance modelInstance, boolean appendDocumentation) {
    LOG.info("Start check");
    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
    result.setFilename(filename);
    BpmnDiagramCheckContext context = new BpmnDiagramCheckContext();
    traverse(modelInstance.getDocument().getRootElement(), result, context);
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
    MessageAppender messageAppender = new MessageAppender();
    context
        .getConvertibles()
        .forEach(
            (element, convertible) -> {
              List<BpmnElementCheckMessage> messages = getMessages(element, result);
              messageAppender.appendMessages(element, messages, appendDocumentation);
              conversions.forEach(conversion -> conversion.convert(element, convertible, messages));
            });
    LOG.info("Done with conversion");
    return result;
  }

  private List<BpmnElementCheckMessage> getMessages(
      DomElement element, BpmnDiagramCheckResult result) {
    return result.getResults().stream()
        .filter(r -> r.getElementId().equals(element.getAttribute("id")))
        .map(BpmnElementCheckResult::getMessages)
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
      DomElement element, BpmnDiagramCheckResult result, BpmnDiagramCheckContext context) {
    DomElementVisitorContext elementContext =
        new DefaultDomElementVisitorContext(element, context, result, notificationService);
    visitors.stream()
        .sorted(Comparator.comparingInt(v -> v instanceof AbstractProcessElementVisitor ? 2 : 3))
        .forEach(visitor -> visitor.visit(elementContext));
    element.getChildElements().forEach(child -> traverse(child, result, context));
  }
}
