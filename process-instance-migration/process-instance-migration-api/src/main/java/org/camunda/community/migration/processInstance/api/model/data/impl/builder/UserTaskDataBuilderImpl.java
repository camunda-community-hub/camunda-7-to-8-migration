package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.UserTaskData;
import org.camunda.community.migration.processInstance.api.model.data.UserTaskData.UserTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.UserTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.ActivityNodeDataImpl;

public class UserTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<UserTaskDataBuilder, UserTaskData, UserTaskDataImpl>
    implements UserTaskDataBuilder {
  @Override
  protected UserTaskDataImpl createData() {
    return new UserTaskDataImpl();
  }

  @Override
  protected UserTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
