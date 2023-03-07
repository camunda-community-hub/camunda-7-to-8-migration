package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface ManualTaskData extends ActivityNodeData {
  interface ManualTaskDataBuilder
      extends ActivityNodeDataBuilder<ManualTaskDataBuilder, ManualTaskData> {}
}
