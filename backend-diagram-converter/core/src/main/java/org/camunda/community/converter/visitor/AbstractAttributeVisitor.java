package org.camunda.community.converter.visitor;

import org.camunda.community.converter.DomElementVisitorContext;

public abstract class AbstractAttributeVisitor extends AbstractFilteringVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    String attribute =
        context.getElement().getAttribute(namespaceUri(context), attributeLocalName());
    visitAttribute(context, attribute);
    if (removeAttribute()) {
      context.addAttributeToRemove(attributeLocalName(), namespaceUri(context));
    }
  }

  private String resolveAttribute(DomElementVisitorContext context) {
    return context.getElement().getAttribute(namespaceUri(context), attributeLocalName());
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return resolveAttribute(context) != null
        && context
            .getElement()
            .getNamespaceURI()
            .equals(context.getProperties().getBpmnNamespace().getUri());
  }

  protected String namespaceUri(DomElementVisitorContext context) {
    return context.getProperties().getCamundaNamespace().getUri();
  }

  public abstract String attributeLocalName();

  protected abstract void visitAttribute(DomElementVisitorContext context, String attribute);

  protected boolean removeAttribute() {
    return true;
  }

  @Override
  protected void logVisit(DomElementVisitorContext context) {
    LOG.debug(
        "Visiting attribute 'camunda:{}' on element '{}:{}'",
        attributeLocalName(),
        context.getElement().getPrefix(),
        context.getElement().getLocalName());
  }
}
