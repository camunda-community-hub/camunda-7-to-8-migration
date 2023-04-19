package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.MultiInstanceData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class MultiInstanceDataVisitor extends AbstractActivityNodeDataVisitor<MultiInstanceData> {
  @Override
  protected void doHandle(TypedActivityNodeDataVisitorContext<MultiInstanceData> context) {
    context
        .getActivityNodeData()
        .getInstances()
        .forEach(instance -> context.addChildData(context.getActivityId(), instance));
    // TODO complete instances according to loop counters
  }

  @Override
  protected Class<MultiInstanceData> getType() {
    return MultiInstanceData.class;
  }
}
