package org.camunda.community.converter.visitor.impl.gateway;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ParallelGatewayConvertible;
import org.camunda.community.converter.visitor.AbstractGatewayVisitor;

public class ParallelGatewayVisitor extends AbstractGatewayVisitor {
  @Override
  public String localName() {
    return "parallelGateway";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ParallelGatewayConvertible();
  }
}
