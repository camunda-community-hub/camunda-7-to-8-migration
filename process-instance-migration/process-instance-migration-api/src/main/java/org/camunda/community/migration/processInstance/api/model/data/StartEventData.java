package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface StartEventData extends CommonActivityNodeData {
  interface StartEventDataBuilder
      extends CommonActivityNodeDataBuilder<StartEventDataBuilder, StartEventData> {}
}
