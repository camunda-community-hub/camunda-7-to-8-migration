package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;

public interface DomElementVisitor {

  void visit(DomElementVisitorContext context);
}
