package org.camunda.community.migration.converter.visitor;

import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;

public abstract class AbstractCamundaAttributeVisitor extends AbstractAttributeVisitor {

  protected List<String> namespaceUri() {
    return List.of(NamespaceUri.CAMUNDA, NamespaceUri.CAMUNDA_DMN);
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
