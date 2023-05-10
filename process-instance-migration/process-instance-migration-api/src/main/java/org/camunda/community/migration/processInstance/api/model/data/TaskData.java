package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface TaskData extends CommonActivityNodeData {
  interface TaskDataBuilder extends CommonActivityNodeDataBuilder<TaskDataBuilder, TaskData> {}
}
