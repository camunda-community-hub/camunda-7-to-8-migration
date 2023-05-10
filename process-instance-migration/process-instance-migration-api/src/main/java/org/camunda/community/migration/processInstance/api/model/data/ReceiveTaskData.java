package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface ReceiveTaskData extends CommonActivityNodeData {
  interface ReceiveTaskDataBuilder
      extends CommonActivityNodeDataBuilder<ReceiveTaskDataBuilder, ReceiveTaskData> {}
}
