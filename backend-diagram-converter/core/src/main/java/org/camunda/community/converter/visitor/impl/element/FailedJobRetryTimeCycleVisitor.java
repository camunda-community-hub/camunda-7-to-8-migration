package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class FailedJobRetryTimeCycleVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    String timecycle = context.getElement().getTextContent();

    return MessageFactory.failedJobRetryTimeCycle(context.getElement().getLocalName(), timecycle);
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }

  @Override
  public String localName() {
    return "failedJobRetryTimeCycle";
  }
}
