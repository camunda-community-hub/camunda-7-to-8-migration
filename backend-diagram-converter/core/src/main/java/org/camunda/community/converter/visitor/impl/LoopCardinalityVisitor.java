package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class LoopCardinalityVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  public String localName() {
    return "loopCardinality";
  }

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    context.addMessage(Severity.WARNING, "Loop cardinality is currently not supported");
  }
}
