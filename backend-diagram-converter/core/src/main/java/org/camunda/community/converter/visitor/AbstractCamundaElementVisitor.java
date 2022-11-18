package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.message.Message;

public abstract class AbstractCamundaElementVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.CAMUNDA;
  }

  @Override
  protected final void visitFilteredElement(DomElementVisitorContext context) {
    Message message = visitCamundaElement(context);
    if (isSilent()) {
      return;
    }
    Severity severity =
        messageSeverity() != null
            ? messageSeverity()
            : canBeTransformed(context) ? Severity.TASK : Severity.WARNING;
    context.addMessage(severity, message);
  }

  protected abstract Message visitCamundaElement(DomElementVisitorContext context);

  public abstract boolean canBeTransformed(DomElementVisitorContext context);

  protected boolean isSilent() {
    return false;
  }

  protected Severity messageSeverity() {
    return null;
  }
}
