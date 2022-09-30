package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;

public abstract class AbstractCamundaElementVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.CAMUNDA;
  }

  @Override
  protected final void visitFilteredElement(DomElementVisitorContext context) {
    String hint = visitCamundaElement(context);
    if (isSilent()) {
      return;
    }
    if (!canBeTransformed(context)) {
      context.addMessage(
          messageSeverity() == null ? Severity.WARNING : messageSeverity(),
          "Element '"
              + context.getElement().getLocalName()
              + "' is currently not supported in Zeebe. Hint: "
              + hint);
    } else {
      context.addMessage(
          messageSeverity() == null ? Severity.TASK : messageSeverity(),
          "Element '"
              + context.getElement().getLocalName()
              + "' was transformed, please review. Hint: "
              + hint);
    }
  }

  protected abstract String visitCamundaElement(DomElementVisitorContext context);

  public abstract boolean canBeTransformed(DomElementVisitorContext context);

  protected boolean isSilent() {
    return false;
  }

  protected Severity messageSeverity() {
    return null;
  }
}
