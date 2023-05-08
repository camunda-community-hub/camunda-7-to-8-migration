package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface StartEventData extends ActivityNodeData {
  interface StartEventDataBuilder
      extends ActivityNodeDataBuilder<StartEventDataBuilder, StartEventData> {}
}
