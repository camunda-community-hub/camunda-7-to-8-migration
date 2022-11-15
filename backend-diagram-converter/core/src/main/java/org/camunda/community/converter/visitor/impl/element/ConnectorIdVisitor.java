package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ConnectorIdVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    context.addConversion(
        ServiceTaskConvertible.class,
        serviceTaskConversion ->
            serviceTaskConversion
                .getZeebeTaskDefinition()
                .setType(context.getElement().getTextContent()));
    return MessageFactory.connectorId(context.getElement().getLocalName());
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }

  @Override
  public String localName() {
    return "connectorId";
  }
}
