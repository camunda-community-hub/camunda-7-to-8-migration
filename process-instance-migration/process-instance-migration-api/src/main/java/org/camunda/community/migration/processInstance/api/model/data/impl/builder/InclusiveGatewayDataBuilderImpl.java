package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.InclusiveGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.InclusiveGatewayData.InclusiveGatewayDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.InclusiveGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class InclusiveGatewayDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        InclusiveGatewayDataBuilder, InclusiveGatewayData, InclusiveGatewayDataImpl>
    implements InclusiveGatewayDataBuilder {
  @Override
  protected InclusiveGatewayDataImpl createData() {
    return new InclusiveGatewayDataImpl();
  }

  @Override
  protected InclusiveGatewayDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
