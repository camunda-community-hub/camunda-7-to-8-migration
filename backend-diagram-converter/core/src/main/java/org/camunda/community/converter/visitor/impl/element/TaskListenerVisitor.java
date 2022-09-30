package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class TaskListenerVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "taskListener";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Task Listeners are not available with Zeebe. Please review them";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
