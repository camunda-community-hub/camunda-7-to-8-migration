package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ReceiveTaskData extends ActivityNodeData {
  interface ReceiveTaskDataBuilder
      extends ActivityNodeDataBuilder<ReceiveTaskDataBuilder, ReceiveTaskData> {}
}
