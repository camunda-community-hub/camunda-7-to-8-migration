package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractDelegateImplementationVisitor;

public class ClassVisitor extends AbstractDelegateImplementationVisitor {

  @Override
  public String attributeLocalName() {
    return "class";
  }
}
