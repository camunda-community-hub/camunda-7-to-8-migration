package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class TaskListenerVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "taskListener";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.taskListener(context.getElement().getLocalName());
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
