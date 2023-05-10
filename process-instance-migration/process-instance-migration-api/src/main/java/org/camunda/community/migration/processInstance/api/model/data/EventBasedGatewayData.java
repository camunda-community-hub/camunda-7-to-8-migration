package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface EventBasedGatewayData extends CommonActivityNodeData {
  interface EventBasedGatewayDataBuilder
      extends CommonActivityNodeDataBuilder<EventBasedGatewayDataBuilder, EventBasedGatewayData> {}
}
