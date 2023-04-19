package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.UserTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class UserTaskDataVisitor extends AbstractActivityNodeDataVisitor <UserTaskData>{@Override protected void doHandle(TypedActivityNodeDataVisitorContext<UserTaskData> context) {
  // nothing to do
  }@Override protected Class<UserTaskData> getType() {
  return UserTaskData.class;
  }}
