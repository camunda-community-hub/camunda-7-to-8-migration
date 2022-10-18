package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class InputOutputVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "inputOutput";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Inputs and outputs are now handled as atomic extension elements";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Severity messageSeverity() {
    return Severity.INFO;
  }
}
