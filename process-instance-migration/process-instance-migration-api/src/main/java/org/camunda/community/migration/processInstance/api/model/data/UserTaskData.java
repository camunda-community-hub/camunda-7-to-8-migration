package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface UserTaskData extends ActivityNodeData {
  interface UserTaskDataBuilder
      extends ActivityNodeDataBuilder<UserTaskDataBuilder, UserTaskData> {}
}
