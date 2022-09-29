package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ConnectorVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "connector";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Connectors will be jobs in Zeebe";
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
