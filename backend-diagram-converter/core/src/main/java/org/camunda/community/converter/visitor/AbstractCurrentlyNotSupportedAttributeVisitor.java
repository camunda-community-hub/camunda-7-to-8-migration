package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;

public abstract class AbstractCurrentlyNotSupportedAttributeVisitor
    extends AbstractAttributeVisitor {

  @Override
  protected final void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(
        Severity.WARNING,
        "Attribute '"
            + attributeLocalName()
            + "' on '"
            + context.getElement().getLocalName()
            + "' is currently not supported in Zeebe");
  }
}
