package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;

public abstract class AbstractCamundaAttributeVisitor extends AbstractAttributeVisitor {

  protected String namespaceUri() {
    return NamespaceUri.CAMUNDA;
  }

  protected boolean removeAttribute(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected void logVisit(DomElementVisitorContext context) {
    LOG.debug(
        "Visiting attribute '{}:{}' on element '{}:{}'",
        namespaceUri(),
        attributeLocalName(),
        context.getElement().getPrefix(),
        context.getElement().getLocalName());
  }
}
