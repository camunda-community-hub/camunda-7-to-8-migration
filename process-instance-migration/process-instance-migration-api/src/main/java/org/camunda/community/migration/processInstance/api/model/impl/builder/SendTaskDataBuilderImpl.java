package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.SendTaskData;
import org.camunda.community.migration.processInstance.api.model.SendTaskData.SendTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.SendTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

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
