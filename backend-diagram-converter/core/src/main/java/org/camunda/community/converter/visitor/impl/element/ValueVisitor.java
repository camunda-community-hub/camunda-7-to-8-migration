package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ValueVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "value";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.value();
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }

  @Override
  protected boolean isSilent() {
    return true;
  }
}
