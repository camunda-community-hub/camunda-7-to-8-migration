package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractRemoveAttributeVisitor;

public class AsyncAfterVisitor extends AbstractRemoveAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "asyncAfter";
  }
}
