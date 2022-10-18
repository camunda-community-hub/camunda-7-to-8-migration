package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.visitor.AbstractFilteringVisitor;

public class Camunda7NamespaceVisitor extends AbstractFilteringVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    context.addElementToRemove();
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return context.getElement().getNamespaceURI().equals(NamespaceUri.CAMUNDA);
  }

  @Override
  protected void logVisit(DomElementVisitorContext context) {
    // do not log at all
  }
}
