package org.camunda.community.converter;

import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.Set;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.DomElementVisitorContext.DefaultDomElementVisitorContext;
import org.camunda.community.converter.conversion.Conversion;
import org.camunda.community.converter.visitor.AbstractProcessElementVisitor;
import org.camunda.community.converter.visitor.DomElementVisitor;
import org.camunda.community.converter.visitor.impl.LoggingVisitor;

public class BpmnConverter {
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
    BpmnDiagramCheckResult result = new BpmnDiagramCheckResult();
    BpmnDiagramCheckContext context = new BpmnDiagramCheckContext();
    traverse(modelInstance.getDocument().getRootElement(), result, context);
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
    result.setBpmnXml(Bpmn.convertToString(modelInstance));
    return result;
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
