package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.MessageEndEventData;
import org.camunda.community.migration.processInstance.api.model.data.MessageEndEventData.MessageEndEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageEndEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class MessageEndEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        MessageEndEventDataBuilder, MessageEndEventData, MessageEndEventDataImpl>
    implements MessageEndEventDataBuilder {
  @Override
  protected MessageEndEventDataImpl createData() {
    return new MessageEndEventDataImpl();
  }

  @Override
  protected MessageEndEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
