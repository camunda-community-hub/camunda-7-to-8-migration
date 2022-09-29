package org.camunda.community.converter.visitor.impl.gateway;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.InclusiveGatewayConvertible;
import org.camunda.community.converter.visitor.AbstractGatewayVisitor;

public class InclusiveGatewayVisitor extends AbstractGatewayVisitor {
  @Override
  public String localName() {
    return "inclusiveGateway";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return false;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new InclusiveGatewayConvertible();
  }
}
