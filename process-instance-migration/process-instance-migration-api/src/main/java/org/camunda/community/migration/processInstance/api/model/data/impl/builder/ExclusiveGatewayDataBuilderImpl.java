package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ExclusiveGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.ExclusiveGatewayData.ExclusiveGatewayDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ExclusiveGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class ExclusiveGatewayDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
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
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
