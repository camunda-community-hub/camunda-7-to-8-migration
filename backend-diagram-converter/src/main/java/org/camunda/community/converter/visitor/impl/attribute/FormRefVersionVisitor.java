package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractCurrentlyNotSupportedAttributeVisitor;

public class FormRefVersionVisitor extends AbstractCurrentlyNotSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "formRefVersion";
  }
}
