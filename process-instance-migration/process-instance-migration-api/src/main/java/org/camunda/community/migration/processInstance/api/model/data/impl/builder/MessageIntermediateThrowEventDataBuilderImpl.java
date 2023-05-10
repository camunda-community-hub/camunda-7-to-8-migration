package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateThrowEventData;
import org.camunda.community.migration.processInstance.api.model.data.MessageIntermediateThrowEventData.MessageIntermediateThrowEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.MessageIntermediateThrowEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class MessageIntermediateThrowEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        MessageIntermediateThrowEventDataBuilder,
        MessageIntermediateThrowEventData,
        MessageIntermediateThrowEventDataImpl>
    implements MessageIntermediateThrowEventDataBuilder {
  @Override
  protected MessageIntermediateThrowEventDataImpl createData() {
    return new MessageIntermediateThrowEventDataImpl();
  }

  @Override
  protected MessageIntermediateThrowEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
