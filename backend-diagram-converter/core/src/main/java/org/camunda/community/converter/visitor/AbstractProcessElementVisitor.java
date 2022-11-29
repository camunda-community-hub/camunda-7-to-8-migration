package org.camunda.community.converter.visitor;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;

public abstract class AbstractProcessElementVisitor extends AbstractBpmnElementVisitor {
  @Override
  protected final void visitBpmnElement(DomElementVisitorContext context) {
    context.setAsBpmnProcessElement(createConvertible(context));
    postCreationVisitor(context);
  }

  protected abstract Convertible createConvertible(DomElementVisitorContext context);

  protected void postCreationVisitor(DomElementVisitorContext context) {
    // do nothing
  }
}
