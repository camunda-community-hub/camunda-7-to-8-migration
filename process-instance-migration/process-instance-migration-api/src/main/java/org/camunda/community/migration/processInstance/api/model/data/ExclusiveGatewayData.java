package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface ExclusiveGatewayData extends CommonActivityNodeData {
  interface ExclusiveGatewayDataBuilder
      extends CommonActivityNodeDataBuilder<ExclusiveGatewayDataBuilder, ExclusiveGatewayData> {}
}
