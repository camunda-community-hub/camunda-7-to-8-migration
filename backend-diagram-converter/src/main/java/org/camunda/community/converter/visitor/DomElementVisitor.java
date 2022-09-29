package org.camunda.community.converter.visitor;

import org.camunda.community.converter.DomElementVisitorContext;

public interface DomElementVisitor {

  void visit(DomElementVisitorContext context);
}
