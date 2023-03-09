package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ScriptTaskData extends ActivityNodeData {
  interface ScriptTaskDataBuilder
      extends ActivityNodeDataBuilder<ScriptTaskDataBuilder, ScriptTaskData> {}
}
