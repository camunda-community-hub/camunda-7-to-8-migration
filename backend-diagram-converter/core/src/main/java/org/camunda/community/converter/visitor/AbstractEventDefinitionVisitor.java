package org.camunda.community.converter.visitor;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.message.MessageFactory;

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
          Severity.INFO, MessageFactory.elementCanBeUsed(context.getElement().getLocalName()));
    } else {
      context.addMessage(
          Severity.WARNING,
          MessageFactory.elementNotSupported(
              context.getElement().getLocalName(), context.getProperties().getPlatformVersion()));
    }
  }

  public abstract boolean canBeConverted(DomElementVisitorContext context);

  protected void visitEventDefinitionElement(DomElementVisitorContext context) {
    // do nothing
  }
}
