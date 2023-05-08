package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class ServiceTaskDataVisitor extends AbstractActivityNodeDataVisitor<ServiceTaskData> {
  @Override
  protected void doHandle(TypedActivityNodeDataVisitorContext<ServiceTaskData> context) {
    // nothing to do here
  }

  @Override
  protected Class<ServiceTaskData> getType() {
    return ServiceTaskData.class;
  }
}
