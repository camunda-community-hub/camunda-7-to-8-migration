package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

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
}
