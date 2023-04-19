package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.ManualTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class ManualTaskDataVisitor extends AbstractActivityNodeDataVisitor <ManualTaskData>{@Override protected void doHandle(TypedActivityNodeDataVisitorContext<ManualTaskData> context) {
  // nothing to do
  }@Override protected Class<ManualTaskData> getType() {
  return ManualTaskData.class;
  }}
