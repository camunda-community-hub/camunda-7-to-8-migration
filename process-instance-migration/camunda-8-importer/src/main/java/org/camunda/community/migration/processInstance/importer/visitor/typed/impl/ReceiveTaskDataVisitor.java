package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class ReceiveTaskDataVisitor extends AbstractActivityNodeDataVisitor<ReceiveTaskData> {
  @Override
  protected void doHandle(TypedActivityNodeDataVisitorContext<ReceiveTaskData> context) {
    // nothing to do here
  }

  @Override
  protected Class<ReceiveTaskData> getType() {
    return ReceiveTaskData.class;
  }
}
