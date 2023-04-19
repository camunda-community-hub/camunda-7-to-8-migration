package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ExclusiveGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.ExclusiveGatewayData.ExclusiveGatewayDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ExclusiveGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class ExclusiveGatewayDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        ExclusiveGatewayDataBuilder, ExclusiveGatewayData, ExclusiveGatewayDataImpl>
    implements ExclusiveGatewayDataBuilder {
  @Override
  protected ExclusiveGatewayDataImpl createData() {
    return new ExclusiveGatewayDataImpl();
  }

  @Override
  protected ExclusiveGatewayDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
