package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface UserTaskData extends CommonActivityNodeData {
  interface UserTaskDataBuilder
      extends CommonActivityNodeDataBuilder<UserTaskDataBuilder, UserTaskData> {}
}
