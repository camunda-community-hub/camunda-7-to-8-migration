package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public interface InclusiveGatewayData extends CommonActivityNodeData {
  interface InclusiveGatewayDataBuilder
      extends CommonActivityNodeDataBuilder<InclusiveGatewayDataBuilder, InclusiveGatewayData> {}
}
