package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ListVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "list";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.list();
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
