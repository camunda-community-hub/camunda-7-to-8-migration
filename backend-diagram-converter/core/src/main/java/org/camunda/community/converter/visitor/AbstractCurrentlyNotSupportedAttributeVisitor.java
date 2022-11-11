package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;

public abstract class AbstractCurrentlyNotSupportedAttributeVisitor
    extends AbstractAttributeVisitor {

  @Override
  protected final void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(
        Severity.WARNING,
        Message.attributeNotSupported(attributeLocalName(), context.getElement().getLocalName()));
  }
}
