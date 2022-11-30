package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.visitor.AbstractCurrentlyNotSupportedAttributeVisitor;

public class CalledElementVersionTagVisitor extends AbstractCurrentlyNotSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "calledElementVersionTag";
  }
}
