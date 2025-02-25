package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.DecisionConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class DecisionVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new DecisionConvertible();
  }

  @Override
  public String localName() {
    return "decision";
  }
}
