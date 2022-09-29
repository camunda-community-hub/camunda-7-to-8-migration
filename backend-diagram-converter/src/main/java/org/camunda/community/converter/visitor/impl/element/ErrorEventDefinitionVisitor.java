package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ErrorEventDefinitionVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "errorEventDefinition";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Error event definitions are not supported in Zeebe. Please review your implementation";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
