package org.camunda.community.migration.processInstance.importer.visitor;

public interface ActivityNodeDataVisitor {
  void handle(ActivityNodeDataVisitorContext context);
}
