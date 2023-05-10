package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.TaskData;
import org.camunda.community.migration.processInstance.api.model.data.TaskData.TaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.TaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class TaskDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<TaskDataBuilder, TaskData, TaskDataImpl>
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
  protected CommonActivityNodeDataImpl data() {
    return data;
  }
}
