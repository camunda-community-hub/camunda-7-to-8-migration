package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.OutputConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class OutputVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new OutputConvertible();
  }

  @Override
  public String localName() {
    return "output";
  }
}
