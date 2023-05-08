package org.camunda.community.migration.processInstance.api.model.data;

import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;

public interface ParallelGatewayData extends ActivityNodeData {

  interface ParallelGatewayDataBuilder
      extends ActivityNodeDataBuilder<ParallelGatewayDataBuilder, ParallelGatewayData> {}
}
