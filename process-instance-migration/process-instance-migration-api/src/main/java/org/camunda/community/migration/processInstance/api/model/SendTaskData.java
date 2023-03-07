package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface SendTaskData extends ActivityNodeData {
  interface SendTaskDataBuilder
      extends ActivityNodeDataBuilder<SendTaskDataBuilder, SendTaskData> {}
}
