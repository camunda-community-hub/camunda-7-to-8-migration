package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.RetryTimeCycleConverter;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class FailedJobRetryTimeCycleVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    String timecycle = context.getElement().getTextContent();
    int retries = RetryTimeCycleConverter.convert(timecycle);
    context.addConversion(
        ServiceTaskConvertible.class,
        convertible -> convertible.getZeebeTaskDefinition().setRetries(retries));
    return MessageFactory.failedJobRetryTimeCycle(
        context.getElement().getLocalName(), timecycle, retries);
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
