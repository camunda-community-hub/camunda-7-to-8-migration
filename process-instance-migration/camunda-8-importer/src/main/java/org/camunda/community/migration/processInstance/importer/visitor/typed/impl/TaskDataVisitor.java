package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.TaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class TaskDataVisitor extends AbstractActivityNodeDataVisitor<TaskData> {

@Override protected void doHandle(TypedActivityNodeDataVisitorContext<TaskData> context) {
  // nothing to do
  }@Override protected Class<TaskData> getType() {
  return TaskData.class;
  }}
