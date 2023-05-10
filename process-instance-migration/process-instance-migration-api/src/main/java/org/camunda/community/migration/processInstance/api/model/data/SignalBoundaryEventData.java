package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SignalBoundaryEventData extends CommonActivityNodeData {
  interface SignalBoundaryEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          SignalBoundaryEventDataBuilder, SignalBoundaryEventData> {}
}
