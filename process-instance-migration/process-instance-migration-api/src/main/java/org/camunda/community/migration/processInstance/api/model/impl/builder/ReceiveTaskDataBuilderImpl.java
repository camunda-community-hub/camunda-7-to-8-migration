package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.ReceiveTaskData;
import org.camunda.community.migration.processInstance.api.model.ReceiveTaskData.ReceiveTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

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
