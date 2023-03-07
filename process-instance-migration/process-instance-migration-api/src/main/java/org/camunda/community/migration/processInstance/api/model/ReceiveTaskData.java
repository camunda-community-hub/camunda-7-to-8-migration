package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface ReceiveTaskData extends ActivityNodeData {
  interface ReceiveTaskDataBuilder
      extends ActivityNodeDataBuilder<ReceiveTaskDataBuilder, ReceiveTaskData> {}
}
