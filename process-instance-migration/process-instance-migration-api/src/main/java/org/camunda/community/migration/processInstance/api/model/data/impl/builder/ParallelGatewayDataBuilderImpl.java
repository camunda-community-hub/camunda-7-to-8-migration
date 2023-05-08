package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ParallelGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.ParallelGatewayData.ParallelGatewayDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ParallelGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class ParallelGatewayDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        ParallelGatewayDataBuilder, ParallelGatewayData, ParallelGatewayDataImpl>
    implements ParallelGatewayDataBuilder {
  @Override
  protected ParallelGatewayDataImpl createData() {
    return new ParallelGatewayDataImpl();
  }

  @Override
  protected ParallelGatewayDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
