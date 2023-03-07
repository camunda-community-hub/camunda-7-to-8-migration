package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface BusinessRuleTaskData extends ActivityNodeData {
  interface BusinessRuleTaskDataBuilder
      extends ActivityNodeDataBuilder<BusinessRuleTaskDataBuilder, BusinessRuleTaskData> {}
}
