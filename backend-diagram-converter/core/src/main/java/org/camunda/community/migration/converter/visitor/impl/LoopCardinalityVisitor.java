package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;

public class LoopCardinalityVisitor extends AbstractBpmnElementVisitor {

  @Override
  public String localName() {
    return "loopCardinality";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {}

  @Override
  protected Message cannotBeConvertedMessage(DomElementVisitorContext context) {
    return MessageFactory.loopCardinality();
  }
}
