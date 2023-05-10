package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.IntermediateThrowEventData;
import org.camunda.community.migration.processInstance.api.model.data.IntermediateThrowEventData.IntermediateThrowEventDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.IntermediateThrowEventDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class IntermediateThrowEventDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        IntermediateThrowEventDataBuilder,
        IntermediateThrowEventData,
        IntermediateThrowEventDataImpl>
    implements IntermediateThrowEventDataBuilder {
  @Override
  protected IntermediateThrowEventDataImpl createData() {
    return new IntermediateThrowEventDataImpl();
  }

  @Override
  protected IntermediateThrowEventDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
