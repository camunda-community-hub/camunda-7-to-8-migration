package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.MessageBoundaryEventData;
import org.camunda.community.migration.processInstance.api.model.data.MessageBoundaryEventData.MessageBoundaryEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageBoundaryEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class MessageBoundaryEventBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        MessageBoundaryEventDataBuilder, MessageBoundaryEventData, MessageBoundaryEventDataImpl>
    implements MessageBoundaryEventDataBuilder {
  @Override
  protected MessageBoundaryEventDataImpl createData() {
    return new MessageBoundaryEventDataImpl();
  }

  @Override
  protected MessageBoundaryEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
