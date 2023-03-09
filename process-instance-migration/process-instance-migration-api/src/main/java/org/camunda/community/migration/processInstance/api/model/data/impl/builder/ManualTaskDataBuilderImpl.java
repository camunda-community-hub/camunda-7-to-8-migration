package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ManualTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ManualTaskData.ManualTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ManualTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class ManualTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<ManualTaskDataBuilder, ManualTaskData, ManualTaskDataImpl>
    implements ManualTaskDataBuilder {
  @Override
  protected ManualTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ManualTaskDataImpl createData() {
    return new ManualTaskDataImpl();
  }
}
