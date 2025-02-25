package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.InputExpressionConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class InputExpressionVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new InputExpressionConvertible();
  }

  @Override
  public String localName() {
    return "inputExpression";
  }
}
