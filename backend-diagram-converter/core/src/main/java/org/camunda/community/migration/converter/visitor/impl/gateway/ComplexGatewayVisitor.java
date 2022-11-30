package org.camunda.community.migration.converter.visitor.impl.gateway;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.ComplexGatewayConvertible;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractGatewayVisitor;

public class ComplexGatewayVisitor extends AbstractGatewayVisitor {
  @Override
  public String localName() {
    return "complexGateway";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ComplexGatewayConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }
}
