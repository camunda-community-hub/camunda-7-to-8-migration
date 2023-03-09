package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ManualTaskData extends ActivityNodeData {
  interface ManualTaskDataBuilder
      extends ActivityNodeDataBuilder<ManualTaskDataBuilder, ManualTaskData> {}
}
