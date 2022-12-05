package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor;

public class ExecutionListenerVisitor extends AbstractListenerVisitor {
  @Override
  public String localName() {
    return "executionListener";
  }

  @Override
  protected Message visitListener(
      DomElementVisitorContext context, String event, String implementation) {
    return MessageFactory.executionListener(event, implementation);
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
