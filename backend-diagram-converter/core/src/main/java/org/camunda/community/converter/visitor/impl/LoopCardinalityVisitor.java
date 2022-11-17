package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class LoopCardinalityVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri(DomElementVisitorContext context) {
    return context.getProperties().getBpmnNamespace().getUri();
  }

  @Override
  public String localName() {
    return "loopCardinality";
  }

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    context.addMessage(Severity.WARNING, MessageFactory.loopCardinality());
  }
}
