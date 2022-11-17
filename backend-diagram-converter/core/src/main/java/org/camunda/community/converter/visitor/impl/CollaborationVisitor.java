package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class CollaborationVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri(DomElementVisitorContext context) {
    return context.getProperties().getBpmnNamespace().getUri();
  }

  @Override
  public String localName() {
    return "collaboration";
  }

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    // just do nothing
  }
}
