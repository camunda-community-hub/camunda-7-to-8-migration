package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface TaskData extends ActivityNodeData {
  interface TaskDataBuilder extends ActivityNodeDataBuilder<TaskDataBuilder, TaskData> {}
}
