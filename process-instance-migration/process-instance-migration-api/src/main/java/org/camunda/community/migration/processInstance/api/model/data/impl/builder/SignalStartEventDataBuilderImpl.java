package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.SignalStartEventData;
import org.camunda.community.migration.processInstance.api.model.data.SignalStartEventData.SignalStartEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalStartEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class SignalStartEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        SignalStartEventDataBuilder, SignalStartEventData, SignalStartEventDataImpl>
    implements SignalStartEventDataBuilder {
  @Override
  protected SignalStartEventDataImpl createData() {
    return new SignalStartEventDataImpl();
  }

  @Override
  protected SignalStartEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
