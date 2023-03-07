package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface UserTaskData extends ActivityNodeData {
  interface UserTaskDataBuilder
      extends ActivityNodeDataBuilder<UserTaskDataBuilder, UserTaskData> {}
}
