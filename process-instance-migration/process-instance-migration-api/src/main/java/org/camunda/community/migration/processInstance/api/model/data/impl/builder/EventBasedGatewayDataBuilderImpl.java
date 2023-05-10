package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.EventBasedGatewayData;
import org.camunda.community.migration.processInstance.api.model.data.EventBasedGatewayData.EventBasedGatewayDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.EventBasedGatewayDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class EventBasedGatewayDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        EventBasedGatewayDataBuilder, EventBasedGatewayData, EventBasedGatewayDataImpl>
    implements EventBasedGatewayDataBuilder {
  @Override
  protected EventBasedGatewayDataImpl createData() {
    return new EventBasedGatewayDataImpl();
  }

  @Override
  protected EventBasedGatewayDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
