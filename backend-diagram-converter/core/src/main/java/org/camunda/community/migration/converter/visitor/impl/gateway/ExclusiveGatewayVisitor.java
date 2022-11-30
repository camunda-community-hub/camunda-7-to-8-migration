package org.camunda.community.migration.converter.visitor.impl.gateway;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ExclusiveGatewayConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractGatewayVisitor;

public class ExclusiveGatewayVisitor extends AbstractGatewayVisitor {
  @Override
  public String localName() {
    return "exclusiveGateway";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ExclusiveGatewayConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
