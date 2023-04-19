package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.SendTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class SendTaskDataVisitor extends AbstractActivityNodeDataVisitor <SendTaskData>{@Override protected void doHandle(TypedActivityNodeDataVisitorContext<SendTaskData> context) {
  // nothing to do
  }@Override protected Class<SendTaskData> getType() {
  return SendTaskData.class;
  }}
