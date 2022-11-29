package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractBpmnElementVisitor;

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
