package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;

public abstract class AbstractRemoveAttributeVisitor extends AbstractAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(
        Severity.INFO,
        "Unused attribute '"
            + attributeLocalName()
            + "' on '"
            + context.getElement().getLocalName()
            + "' was removed");
  }
}
