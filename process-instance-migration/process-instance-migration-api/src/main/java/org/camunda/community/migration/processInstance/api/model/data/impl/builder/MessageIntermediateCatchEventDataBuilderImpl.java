package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateCatchEventData;
import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateCatchEventData.MessageIntermediateCatchEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageIntermediateCatchEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class MessageIntermediateCatchEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        MessageIntermediateCatchEventDataBuilder,
        MessageIntermediateCatchEventData,
        MessageIntermediateCatchEventDataImpl>
    implements MessageIntermediateCatchEventDataBuilder {
  @Override
  protected MessageIntermediateCatchEventDataImpl createData() {
    return new MessageIntermediateCatchEventDataImpl();
  }

  @Override
  protected MessageIntermediateCatchEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
