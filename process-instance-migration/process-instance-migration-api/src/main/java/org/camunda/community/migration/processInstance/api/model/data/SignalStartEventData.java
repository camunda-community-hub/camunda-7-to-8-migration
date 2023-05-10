package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SignalStartEventData extends CommonActivityNodeData {
  interface SignalStartEventDataBuilder
      extends CommonActivityNodeDataBuilder<SignalStartEventDataBuilder, SignalStartEventData> {}
}
