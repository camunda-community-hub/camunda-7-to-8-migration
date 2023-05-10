package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.SignalIntermediateCatchEventData;
import org.camunda.community.migration.processInstance.api.model.data.SignalIntermediateCatchEventData.SignalIntermediateCatchEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalIntermediateCatchEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class SignalIntermediateCatchEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        SignalIntermediateCatchEventDataBuilder,
        SignalIntermediateCatchEventData,
        SignalIntermediateCatchEventDataImpl>
    implements SignalIntermediateCatchEventDataBuilder {
  @Override
  protected SignalIntermediateCatchEventDataImpl createData() {
    return new SignalIntermediateCatchEventDataImpl();
  }

  @Override
  protected SignalIntermediateCatchEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
