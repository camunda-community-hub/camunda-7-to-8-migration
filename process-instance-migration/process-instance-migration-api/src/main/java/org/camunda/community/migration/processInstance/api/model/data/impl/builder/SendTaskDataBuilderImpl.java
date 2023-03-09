package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.SendTaskData;
import org.camunda.community.migration.processInstance.api.model.data.SendTaskData.SendTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.SendTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class SendTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<SendTaskDataBuilder, SendTaskData, SendTaskDataImpl>
    implements SendTaskDataBuilder {
  @Override
  protected SendTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected SendTaskDataImpl createData() {
    return new SendTaskDataImpl();
  }
}
