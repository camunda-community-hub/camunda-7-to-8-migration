package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ConnectorVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "connector";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.connectorHint();
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
