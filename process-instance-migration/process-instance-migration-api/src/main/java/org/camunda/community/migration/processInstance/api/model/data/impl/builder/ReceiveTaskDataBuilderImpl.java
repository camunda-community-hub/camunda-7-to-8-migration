package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData.ReceiveTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class ReceiveTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        ReceiveTaskDataBuilder, ReceiveTaskData, ReceiveTaskDataImpl>
    implements ReceiveTaskDataBuilder {
  @Override
  protected ReceiveTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ReceiveTaskDataImpl createData() {
    return new ReceiveTaskDataImpl();
  }
}
