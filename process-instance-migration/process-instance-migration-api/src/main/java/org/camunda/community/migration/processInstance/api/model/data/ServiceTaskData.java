package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ServiceTaskData extends ActivityNodeData {
  interface ServiceTaskDataBuilder
      extends ActivityNodeDataBuilder<ServiceTaskDataBuilder, ServiceTaskData> {}
}
