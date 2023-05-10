package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface ServiceTaskData extends CommonActivityNodeData {
  interface ServiceTaskDataBuilder
      extends CommonActivityNodeDataBuilder<ServiceTaskDataBuilder, ServiceTaskData> {}
}
