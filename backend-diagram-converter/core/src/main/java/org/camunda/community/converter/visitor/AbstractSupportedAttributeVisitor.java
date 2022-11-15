package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;

public abstract class AbstractSupportedAttributeVisitor extends AbstractAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    Message message = visitSupportedAttribute(context, attribute);
    context.addMessage(Severity.TASK, message);
  }

  protected abstract Message visitSupportedAttribute(
      DomElementVisitorContext context, String attribute);
}
