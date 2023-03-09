package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface SendTaskData extends ActivityNodeData {
  interface SendTaskDataBuilder
      extends ActivityNodeDataBuilder<SendTaskDataBuilder, SendTaskData> {}
}
