package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.InputConvertible;
import org.camunda.community.migration.converter.visitor.AbstractDecisionElementVisitor;

public class InputVisitor extends AbstractDecisionElementVisitor {
  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new InputConvertible();
  }

  @Override
  public String localName() {
    return "input";
  }
}
