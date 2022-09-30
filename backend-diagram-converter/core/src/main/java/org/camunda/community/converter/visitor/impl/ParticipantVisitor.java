package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class ParticipantVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  public String localName() {
    return "participant";
  }

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    // do nothing
  }
}
