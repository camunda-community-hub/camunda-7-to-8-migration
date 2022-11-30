package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.visitor.AbstractRemoveAttributeVisitor;

public class AsyncBeforeVisitor extends AbstractRemoveAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "asyncBefore";
  }
}
