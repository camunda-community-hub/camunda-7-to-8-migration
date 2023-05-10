package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SignalEndEventData extends CommonActivityNodeData {
  interface SignalEndEventDataBuilder
      extends CommonActivityNodeDataBuilder<SignalEndEventDataBuilder, SignalEndEventData> {}
}
