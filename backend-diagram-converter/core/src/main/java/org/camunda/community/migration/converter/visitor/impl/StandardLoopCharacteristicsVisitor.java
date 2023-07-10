package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;

public class StandardLoopCharacteristicsVisitor extends AbstractBpmnElementVisitor {
  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {
    // nothing to do
  }

  @Override
  public String localName() {
    return "standardLoopCharacteristics";
  }
}
