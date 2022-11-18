package org.camunda.community.converter.visitor;

import org.camunda.community.converter.DomElementVisitorContext;

public abstract class AbstractElementVisitor extends AbstractFilteringVisitor {

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return namespaceUri().equals(context.getElement().getNamespaceURI())
        && localName().equals(context.getElement().getLocalName());
  }

  protected abstract String namespaceUri();

  public abstract String localName();
}
