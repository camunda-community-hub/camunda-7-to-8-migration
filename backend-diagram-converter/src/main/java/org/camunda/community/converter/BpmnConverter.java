package org.camunda.community.converter;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.impl.util.ModelIoException;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.DomElementVisitorContext.DefaultDomElementVisitorContext;
import org.camunda.community.converter.conversion.Conversion;
import org.camunda.community.converter.visitor.AbstractProcessElementVisitor;
import org.camunda.community.converter.visitor.DomElementVisitor;
import org.camunda.community.converter.visitor.impl.LoggingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.Set;

public class BpmnConverter {
  private static final Logger LOG = LoggerFactory.getLogger(BpmnConverter.class);
  private final Set<DomElementVisitor> visitors;
  private final Set<Conversion> conversions;

  public BpmnConverter(Set<DomElementVisitor> visitors, Set<Conversion> conversions) {
    this.visitors = visitors;
    this.conversions = conversions;
  }

  public BpmnModelInstance convert(BpmnDiagramCheckResult result) {
    BpmnModelInstance modelInstance =
        Bpmn.readModelFromStream(new ByteArrayInputStream(result.getBpmnXml().getBytes()));
    result
        .getResults()
        .forEach(
            (elementCheckResult) ->
                conversions.forEach(
                    conversion ->
                        conversion.convert(
                            modelInstance
                                .getDocument()
                                .getElementById(elementCheckResult.getElementId()),
                            elementCheckResult.getConvertible())));
    return modelInstance;
  }

  public BpmnDiagramCheckResult check(BpmnModelInstance modelInstance) {
    LOG.info("Start check");
    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
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
    LOG.info("Start printing bpmn xml");
    String bpmnXml = printXml(modelInstance.getDocument(),false);
    LOG.debug(bpmnXml);
    LOG.info("Done printing bpmn xml");
    result.setBpmnXml(bpmnXml);
    return result;
  }

  public String printXml(DomDocument document, boolean prettyPrint) {
    StringWriter stringWriter = new StringWriter();
    StreamResult result = new StreamResult(stringWriter);
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
    return stringWriter.toString();
  }

  private void traverse(
      DomElement element, BpmnDiagramCheckResult result, BpmnDiagramCheckContext context) {
    DomElementVisitorContext elementContext =
        new DefaultDomElementVisitorContext(element, context, result);
    visitors.stream()
        .sorted(
            Comparator.comparingInt(
                v ->
                    v instanceof LoggingVisitor
                        ? 1
                        : v instanceof AbstractProcessElementVisitor ? 2 : 3))
        .forEach(visitor -> visitor.visit(elementContext));
    element.getChildElements().forEach(child -> traverse(child, result, context));
  }
}
