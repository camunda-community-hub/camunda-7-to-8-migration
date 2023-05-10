package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ReceiveTaskData.ReceiveTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ReceiveTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class ReceiveTaskDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        ReceiveTaskDataBuilder, ReceiveTaskData, ReceiveTaskDataImpl>
    implements ReceiveTaskDataBuilder {
  @Override
  protected ReceiveTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ReceiveTaskDataImpl createData() {
    return new ReceiveTaskDataImpl();
  }
}
