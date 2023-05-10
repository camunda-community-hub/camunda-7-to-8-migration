package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData.ServiceTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ServiceTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class ServiceTaskDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        ServiceTaskDataBuilder, ServiceTaskData, ServiceTaskDataImpl>
    implements ServiceTaskDataBuilder {
  @Override
  protected ServiceTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ServiceTaskDataImpl createData() {
    return new ServiceTaskDataImpl();
  }
}
