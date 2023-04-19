package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ExclusiveGatewayData extends ActivityNodeData {
  interface ExclusiveGatewayDataBuilder
      extends ActivityNodeDataBuilder<ExclusiveGatewayDataBuilder, ExclusiveGatewayData> {}
}
