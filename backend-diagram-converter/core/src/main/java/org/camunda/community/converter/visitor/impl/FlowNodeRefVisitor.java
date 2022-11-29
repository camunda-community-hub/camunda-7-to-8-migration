package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractBpmnElementVisitor;

public class FlowNodeRefVisitor extends AbstractBpmnElementVisitor {
  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {}

  @Override
  public String localName() {
    return "flowNodeRef";
  }
}
