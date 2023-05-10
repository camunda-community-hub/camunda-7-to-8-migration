package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.StartEventData;
import org.camunda.community.migration.processInstance.api.model.data.StartEventData.StartEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.StartEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class StartEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        StartEventDataBuilder, StartEventData, StartEventDataImpl>
    implements StartEventDataBuilder {
  @Override
  protected StartEventDataImpl createData() {
    return new StartEventDataImpl();
  }

  @Override
  protected StartEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
