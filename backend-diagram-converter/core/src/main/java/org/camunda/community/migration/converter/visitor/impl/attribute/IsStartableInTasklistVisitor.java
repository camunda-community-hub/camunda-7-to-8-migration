package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.visitor.AbstractCurrentlyNotSupportedAttributeVisitor;

public class IsStartableInTasklistVisitor extends AbstractCurrentlyNotSupportedAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "isStartableInTasklist";
  }
}
