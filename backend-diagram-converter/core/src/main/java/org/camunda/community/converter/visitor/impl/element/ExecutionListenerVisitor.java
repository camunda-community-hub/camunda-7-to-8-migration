package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ExecutionListenerVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "executionListener";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Execution Listeners are not available with Zeebe. Please review them";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
