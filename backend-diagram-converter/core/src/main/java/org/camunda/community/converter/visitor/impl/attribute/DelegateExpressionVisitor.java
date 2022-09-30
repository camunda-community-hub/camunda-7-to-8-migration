package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractDelegateImplementationVisitor;

public class DelegateExpressionVisitor extends AbstractDelegateImplementationVisitor {

  @Override
  public String attributeLocalName() {
    return "delegateExpression";
  }
}
