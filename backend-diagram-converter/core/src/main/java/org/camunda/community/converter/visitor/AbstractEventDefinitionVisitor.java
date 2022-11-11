package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.message.Message;

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
          Severity.INFO, Message.elementCanBeUsed(context.getElement().getLocalName()));
    } else {
      context.addMessage(
          Severity.WARNING, Message.elementNotSupported(context.getElement().getLocalName()));
    }
  }

  public abstract boolean canBeConverted(DomElementVisitorContext context);

  protected void visitEventDefinitionElement(DomElementVisitorContext context) {
    // do nothing
  }
}
