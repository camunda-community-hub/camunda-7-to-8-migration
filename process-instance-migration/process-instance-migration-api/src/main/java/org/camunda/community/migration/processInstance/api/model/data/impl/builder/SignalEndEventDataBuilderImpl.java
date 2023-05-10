package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.SignalEndEventData;
import org.camunda.community.migration.processInstance.api.model.data.SignalEndEventData.SignalEndEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalEndEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class SignalEndEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        SignalEndEventDataBuilder, SignalEndEventData, SignalEndEventDataImpl>
    implements SignalEndEventDataBuilder {
  @Override
  protected SignalEndEventDataImpl createData() {
    return new SignalEndEventDataImpl();
  }

  @Override
  protected SignalEndEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
