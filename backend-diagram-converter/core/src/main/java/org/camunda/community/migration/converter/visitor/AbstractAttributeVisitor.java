package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;

public abstract class AbstractAttributeVisitor extends AbstractFilteringVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    String attribute = context.getElement().getAttribute(namespaceUri(), attributeLocalName());
    visitAttribute(context, attribute);
    if (removeAttribute()) {
      context.addAttributeToRemove(attributeLocalName(), namespaceUri());
    }
  }

  private String resolveAttribute(DomElementVisitorContext context) {
    return context.getElement().getAttribute(namespaceUri(), attributeLocalName());
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return resolveAttribute(context) != null
        && context.getElement().getNamespaceURI().equals(NamespaceUri.BPMN);
  }

  protected String namespaceUri() {
    return NamespaceUri.CAMUNDA;
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
