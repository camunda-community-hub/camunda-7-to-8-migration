package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.visitor.AbstractCurrentlyNotSupportedAttributeVisitor;

public class CaseRefVisitor extends AbstractCurrentlyNotSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "caseRef";
  }
}
