package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ServiceTaskData.ServiceTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ServiceTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class ServiceTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<
        ServiceTaskDataBuilder, ServiceTaskData, ServiceTaskDataImpl>
    implements ServiceTaskDataBuilder {
  @Override
  protected ServiceTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ServiceTaskDataImpl createData() {
    return new ServiceTaskDataImpl();
  }
}
