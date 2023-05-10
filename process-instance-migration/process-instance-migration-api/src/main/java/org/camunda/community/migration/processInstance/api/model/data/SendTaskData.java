package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SendTaskData extends CommonActivityNodeData {
  interface SendTaskDataBuilder
      extends CommonActivityNodeDataBuilder<SendTaskDataBuilder, SendTaskData> {}
}
