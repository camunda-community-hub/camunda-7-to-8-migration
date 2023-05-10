package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface ScriptTaskData extends CommonActivityNodeData {
  interface ScriptTaskDataBuilder
      extends CommonActivityNodeDataBuilder<ScriptTaskDataBuilder, ScriptTaskData> {}
}
