package org.camunda.community.migration.processInstance.importer.visitor.typed.impl;

import org.camunda.community.migration.processInstance.api.model.data.BusinessRuleTaskData;
import org.camunda.community.migration.processInstance.importer.visitor.typed.AbstractActivityNodeDataVisitor;
import org.camunda.community.migration.processInstance.importer.visitor.typed.TypedActivityNodeDataVisitorContext;

public class BusinessRuleTaskDataVisitor
    extends AbstractActivityNodeDataVisitor<BusinessRuleTaskData> {
  @Override
  protected void doHandle(TypedActivityNodeDataVisitorContext<BusinessRuleTaskData> context) {
    // nothing to do
  }

  @Override
  protected Class<BusinessRuleTaskData> getType() {
    return BusinessRuleTaskData.class;
  }
}
