package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ErrorEventDefinitionVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "errorEventDefinition";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.errorEventDefinition(context.getElement().getLocalName());
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
