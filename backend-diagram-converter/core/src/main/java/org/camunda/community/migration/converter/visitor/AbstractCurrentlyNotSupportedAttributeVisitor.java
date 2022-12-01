package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.MessageFactory;

public abstract class AbstractCurrentlyNotSupportedAttributeVisitor
    extends AbstractAttributeVisitor {

  @Override
  protected final void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(
        Severity.WARNING,
        MessageFactory.attributeNotSupported(
            attributeLocalName(), context.getElement().getLocalName(), attribute));
  }
}
