package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SignalIntermediateThrowEventData extends CommonActivityNodeData {
  interface SignalIntermediateThrowEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          SignalIntermediateThrowEventDataBuilder, SignalIntermediateThrowEventData> {}
}
