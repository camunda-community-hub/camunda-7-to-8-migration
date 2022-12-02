package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractEventRefVisitor extends AbstractBpmnAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.references(attribute);
  }
}
