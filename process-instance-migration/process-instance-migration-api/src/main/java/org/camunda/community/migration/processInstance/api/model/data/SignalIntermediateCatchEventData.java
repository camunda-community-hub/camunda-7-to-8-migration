package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface SignalIntermediateCatchEventData extends CommonActivityNodeData {
  interface SignalIntermediateCatchEventDataBuilder
      extends CommonActivityNodeDataBuilder<
          SignalIntermediateCatchEventDataBuilder, SignalIntermediateCatchEventData> {}
}
