package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.OutputEntryConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class OutputEntryVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new OutputEntryConvertible();
  }

  @Override
  public String localName() {
    return "outputEntry";
  }
}
