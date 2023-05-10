package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.EndEventData;
import org.camunda.community.migration.processInstance.api.model.data.EndEventData.EndEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.EndEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class EndEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<EndEventDataBuilder, EndEventData, EndEventDataImpl>
    implements EndEventDataBuilder {
  @Override
  protected EndEventDataImpl createData() {
    return new EndEventDataImpl();
  }

  @Override
  protected EndEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
