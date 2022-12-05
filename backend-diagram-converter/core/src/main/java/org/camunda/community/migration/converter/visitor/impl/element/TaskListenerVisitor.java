package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor;

public class TaskListenerVisitor extends AbstractListenerVisitor {
  @Override
  public String localName() {
    return "taskListener";
  }

  @Override
  protected Message visitListener(
      DomElementVisitorContext context, String event, String implementation) {
    return MessageFactory.taskListener(event, implementation);
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
