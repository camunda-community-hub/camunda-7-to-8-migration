package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.DecisionTableConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class DecisionTableVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new DecisionTableConvertible();
  }

  @Override
  public String localName() {
    return "decisionTable";
  }
}
