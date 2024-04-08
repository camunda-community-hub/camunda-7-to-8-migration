package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.visitor.AbstractEventRefVisitor;

public class MessageRefVisitor extends AbstractEventRefVisitor {
  @Override
  public String attributeLocalName() {
    return "messageRef";
  }

  @Override
  protected boolean removeAttribute(DomElementVisitorContext context) {
    if (isEndEvent(context.getElement()) || isIntermediateThrowEvent(context.getElement())) {
      return true;
    }
    return false;
  }

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    if (isEndEvent(context.getElement()) || isIntermediateThrowEvent(context.getElement())) {
      return;
    }
    super.visitAttribute(context, attribute);
  }
}
