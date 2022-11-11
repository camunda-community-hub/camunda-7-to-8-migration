package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;

public abstract class AbstractRemoveAttributeVisitor extends AbstractAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(
        Severity.INFO,
        Message.attributeRemoved(attributeLocalName(), context.getElement().getLocalName()));
  }
}
