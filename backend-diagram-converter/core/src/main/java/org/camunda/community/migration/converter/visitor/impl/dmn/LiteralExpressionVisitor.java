package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.LiteralExpressionConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class LiteralExpressionVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new LiteralExpressionConvertible();
  }

  @Override
  public String localName() {
    return "literalExpression";
  }
}
