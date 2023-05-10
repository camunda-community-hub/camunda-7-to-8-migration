package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.SignalIntermediateThrowEventData;
import org.camunda.community.migration.processInstance.api.model.data.SignalIntermediateThrowEventData.SignalIntermediateThrowEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalIntermediateThrowEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class SignalIntermediateThrowEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        SignalIntermediateThrowEventDataBuilder,
        SignalIntermediateThrowEventData,
        SignalIntermediateThrowEventDataImpl>
    implements SignalIntermediateThrowEventDataBuilder {
  @Override
  protected SignalIntermediateThrowEventDataImpl createData() {
    return new SignalIntermediateThrowEventDataImpl();
  }

  @Override
  protected SignalIntermediateThrowEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
