package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface EventBasedGatewayData extends ActivityNodeData {
  interface EventBasedGatewayDataBuilder
      extends ActivityNodeDataBuilder<EventBasedGatewayDataBuilder, EventBasedGatewayData> {}
}
