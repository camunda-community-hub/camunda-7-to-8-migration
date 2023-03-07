package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.TaskData;
import org.camunda.community.migration.processInstance.api.model.TaskData.TaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.TaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class TaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<TaskDataBuilder, TaskData, TaskDataImpl>
    implements TaskDataBuilder {

  @Override
  protected TaskDataImpl createData() {
    return new TaskDataImpl();
  }

  @Override
  protected TaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }
}
