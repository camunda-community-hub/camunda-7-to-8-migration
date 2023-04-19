package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateCatchEventData;
import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateCatchEventData.MessageIntermediateCatchEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageIntermediateCatchEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class MessageIntermediateCatchEventDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
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
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
