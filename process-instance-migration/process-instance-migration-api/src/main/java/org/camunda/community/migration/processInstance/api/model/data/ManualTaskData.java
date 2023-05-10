package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface ManualTaskData extends CommonActivityNodeData {
  interface ManualTaskDataBuilder
      extends CommonActivityNodeDataBuilder<ManualTaskDataBuilder, ManualTaskData> {}
}
