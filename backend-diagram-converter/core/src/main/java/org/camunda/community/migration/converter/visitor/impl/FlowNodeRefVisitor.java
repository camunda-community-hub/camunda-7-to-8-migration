package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;

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
