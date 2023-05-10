package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface BusinessRuleTaskData extends CommonActivityNodeData {
  interface BusinessRuleTaskDataBuilder
      extends CommonActivityNodeDataBuilder<BusinessRuleTaskDataBuilder, BusinessRuleTaskData> {}
}
