package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.InputEntryConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class InputEntryVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new InputEntryConvertible();
  }

  @Override
  public String localName() {
    return "inputEntry";
  }
}
