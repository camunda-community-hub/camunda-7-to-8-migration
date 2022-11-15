package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.MessageFactory;

public abstract class AbstractRemoveAttributeVisitor extends AbstractAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(
        Severity.INFO,
        MessageFactory.attributeRemoved(attributeLocalName(), context.getElement().getLocalName()));
  }
}
