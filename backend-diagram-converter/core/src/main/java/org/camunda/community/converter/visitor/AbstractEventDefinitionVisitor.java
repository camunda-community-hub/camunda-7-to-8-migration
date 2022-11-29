package org.camunda.community.converter.visitor;

import org.camunda.community.converter.DomElementVisitorContext;

public abstract class AbstractEventDefinitionVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected final void visitBpmnElement(DomElementVisitorContext context) {
    // do nothing
  }
}
