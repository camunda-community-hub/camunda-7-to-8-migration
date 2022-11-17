package org.camunda.community.converter.visitor.impl;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class DefinitionsVisitor extends AbstractElementVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    String executionPlatform =
        element.getAttribute(
            context.getProperties().getModelerNamespace().getUri(),
            context.getProperties().getExecutionPlatformVersionAttribute().getName());
    if (executionPlatform != null && executionPlatform.startsWith("8")) {
      throw new RuntimeException("This diagram is already a Camunda 8 diagram");
    }
    element.registerNamespace(
        context.getProperties().getZeebeNamespace().getName(),
        context.getProperties().getZeebeNamespace().getUri());
    element.registerNamespace(
        context.getProperties().getConversionNamespace().getName(),
        context.getProperties().getConversionNamespace().getUri());
    element.setAttribute(
        ConverterProperties.resolveNamespace(
                context.getProperties().getExecutionPlatformAttribute().getNamespace())
            .getUri(),
        context.getProperties().getExecutionPlatformAttribute().getName(),
        context.getProperties().getExecutionPlatformAttribute().getValue());
    element.setAttribute(
        ConverterProperties.resolveNamespace(
                context.getProperties().getExecutionPlatformVersionAttribute().getNamespace())
            .getUri(),
        context.getProperties().getExecutionPlatformVersionAttribute().getName(),
        context.getProperties().getExecutionPlatformVersionAttribute().getValue());
  }

  @Override
  protected String namespaceUri(DomElementVisitorContext context) {
    return context.getProperties().getBpmnNamespace().getUri();
  }

  @Override
  public String localName() {
    return "definitions";
  }
}
