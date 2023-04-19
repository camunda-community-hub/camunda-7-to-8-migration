package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.CallActivityData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class CallActivityDataVisitor extends AbstractActivityNodeDataVisitor<CallActivityData> {
  @Override
  protected void doHandle(TypedActivityNodeDataVisitorContext<CallActivityData> context) {

    // TODO find the created process instance and modify its state
  }

  @Override
  protected Class<CallActivityData> getType() {
    return CallActivityData.class;
  }
}
