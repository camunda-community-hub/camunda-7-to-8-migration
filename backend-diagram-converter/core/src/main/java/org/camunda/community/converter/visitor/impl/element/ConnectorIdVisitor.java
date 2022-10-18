package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ConnectorIdVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    context.addConversion(
        ServiceTaskConvertible.class,
        serviceTaskConversion ->
            serviceTaskConversion
                .getZeebeTaskDefinition()
                .setType(context.getElement().getTextContent()));
    return "The connector id is transformed to a zeebe job type";
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
