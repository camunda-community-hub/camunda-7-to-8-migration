package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.MessageStartEventData;
import org.camunda.community.migration.processInstance.api.model.data.MessageStartEventData.MessageStartEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageStartEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class MessageStartEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        MessageStartEventDataBuilder, MessageStartEventData, MessageStartEventDataImpl>
    implements MessageStartEventDataBuilder {
  @Override
  protected MessageStartEventDataImpl createData() {
    return new MessageStartEventDataImpl();
  }

  @Override
  protected MessageStartEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
