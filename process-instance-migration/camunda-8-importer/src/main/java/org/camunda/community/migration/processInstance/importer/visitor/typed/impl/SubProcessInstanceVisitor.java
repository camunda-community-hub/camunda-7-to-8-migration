package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.SubProcessData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class SubProcessInstanceVisitor extends AbstractActivityNodeDataVisitor<SubProcessData> {
  @Override
  protected void doHandle(TypedActivityNodeDataVisitorContext<SubProcessData> context) {
    // TODO implement this
  }

  @Override
  protected Class<SubProcessData> getType() {
    return SubProcessData.class;
  }
}
