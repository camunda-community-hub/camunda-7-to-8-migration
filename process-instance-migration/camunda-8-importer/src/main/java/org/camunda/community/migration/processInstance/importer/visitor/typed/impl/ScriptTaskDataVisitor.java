package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.ScriptTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class ScriptTaskDataVisitor extends AbstractActivityNodeDataVisitor<ScriptTaskData> {

@Override protected void doHandle(TypedActivityNodeDataVisitorContext<ScriptTaskData> context) {
// nothing to do
  }@Override protected Class<ScriptTaskData> getType() {
  return ScriptTaskData.class;
  }}
