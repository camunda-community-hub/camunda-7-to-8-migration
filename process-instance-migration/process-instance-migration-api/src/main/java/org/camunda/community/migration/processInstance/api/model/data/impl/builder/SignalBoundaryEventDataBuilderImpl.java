package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.SignalBoundaryEventData;
import org.camunda.community.migration.processInstance.api.model.data.SignalBoundaryEventData.SignalBoundaryEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.SignalBoundaryEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class SignalBoundaryEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        SignalBoundaryEventDataBuilder, SignalBoundaryEventData, SignalBoundaryEventDataImpl>
    implements SignalBoundaryEventDataBuilder {
  @Override
  protected SignalBoundaryEventDataImpl createData() {
    return new SignalBoundaryEventDataImpl();
  }

  @Override
  protected SignalBoundaryEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
