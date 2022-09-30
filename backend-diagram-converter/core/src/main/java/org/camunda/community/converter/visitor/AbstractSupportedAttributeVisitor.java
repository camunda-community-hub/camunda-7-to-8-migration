package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;

public abstract class AbstractSupportedAttributeVisitor extends AbstractAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    String hint = visitSupportedAttribute(context, attribute);
    context.addMessage(
        Severity.TASK,
        "Attribute '"
            + attributeLocalName()
            + "' on '"
            + context.getElement().getLocalName()
            + "' was mapped, please review. Hint: "
            + hint);
  }

  protected abstract String visitSupportedAttribute(
      DomElementVisitorContext context, String attribute);
}
