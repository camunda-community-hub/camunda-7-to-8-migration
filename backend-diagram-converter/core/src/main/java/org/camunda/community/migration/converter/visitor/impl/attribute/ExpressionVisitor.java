package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.visitor.AbstractDelegateImplementationVisitor;

public class ExpressionVisitor extends AbstractDelegateImplementationVisitor {
  @Override
  public String attributeLocalName() {
    return "expression";
  }
}
