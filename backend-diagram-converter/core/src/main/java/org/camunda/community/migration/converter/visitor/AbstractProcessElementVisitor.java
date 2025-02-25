package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;

public abstract class AbstractProcessElementVisitor extends AbstractBpmnElementVisitor {
  @Override
  protected final void visitBpmnElement(DomElementVisitorContext context) {
    context.setAsDiagramElement(createConvertible(context));
    postCreationVisitor(context);
  }

  protected abstract Convertible createConvertible(DomElementVisitorContext context);

  protected void postCreationVisitor(DomElementVisitorContext context) {
    // do nothing
  }
}
