package org.camunda.community.migration.processInstance.api.model;

import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public interface ScriptTaskData extends ActivityNodeData {
  interface ScriptTaskDataBuilder
      extends ActivityNodeDataBuilder<ScriptTaskDataBuilder, ScriptTaskData> {}
}
