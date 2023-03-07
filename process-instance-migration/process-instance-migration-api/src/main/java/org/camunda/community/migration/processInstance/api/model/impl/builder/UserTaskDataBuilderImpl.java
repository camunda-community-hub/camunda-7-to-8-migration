package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.UserTaskData;
import org.camunda.community.migration.processInstance.api.model.UserTaskData.UserTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.UserTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

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
