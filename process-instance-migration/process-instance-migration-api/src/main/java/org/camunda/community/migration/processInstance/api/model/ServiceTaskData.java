package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface ServiceTaskData extends ActivityNodeData {
  interface ServiceTaskDataBuilder
      extends ActivityNodeDataBuilder<ServiceTaskDataBuilder, ServiceTaskData> {}
}
