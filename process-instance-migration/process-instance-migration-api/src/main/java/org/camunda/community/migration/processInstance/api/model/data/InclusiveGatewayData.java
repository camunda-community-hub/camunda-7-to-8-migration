package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface InclusiveGatewayData extends ActivityNodeData {
  interface InclusiveGatewayDataBuilder
      extends ActivityNodeDataBuilder<InclusiveGatewayDataBuilder, InclusiveGatewayData> {}
}
