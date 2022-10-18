package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;

public abstract class AbstractEventDefinitionVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  protected final void visitFilteredElement(DomElementVisitorContext context) {
    visitEventDefinitionElement(context);
    if (canBeConverted(context)) {
      context.addMessage(
          Severity.INFO,
          "Element '" + context.getElement().getLocalName() + "' can be used in Zeebe");
    } else {
      context.addMessage(
          Severity.WARNING,
          "Element '"
              + context.getElement().getLocalName()
              + "' is currently not supported in Zeebe");
    }
  }

  public abstract boolean canBeConverted(DomElementVisitorContext context);

  protected void visitEventDefinitionElement(DomElementVisitorContext context) {
    // by default, nothing happens
  }
}
