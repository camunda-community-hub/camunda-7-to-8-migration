package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class FailedJobRetryTimeCycleVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Retry time cycle works different in Zeebe. Please review your implementation";
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
