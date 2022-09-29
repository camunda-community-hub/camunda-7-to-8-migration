package org.camunda.community.converter.visitor.impl.gateway;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.EventBasedGatewayConvertible;
import org.camunda.community.converter.visitor.AbstractGatewayVisitor;

public class EventBasedGatewayVisitor extends AbstractGatewayVisitor {
  @Override
  public String localName() {
    return "eventBasedGateway";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new EventBasedGatewayConvertible();
  }
}
