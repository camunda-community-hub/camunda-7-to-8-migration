package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractDelegateImplementationVisitor;

public class ExpressionVisitor extends AbstractDelegateImplementationVisitor {
  @Override
  public String attributeLocalName() {
    return "expression";
  }
}
