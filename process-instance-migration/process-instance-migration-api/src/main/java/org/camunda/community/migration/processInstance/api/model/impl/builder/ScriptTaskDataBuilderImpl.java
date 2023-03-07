package org.camunda.community.migration.processInstance.api.model.impl.builder;

import org.camunda.community.migration.processInstance.api.model.ScriptTaskData;
import org.camunda.community.migration.processInstance.api.model.ScriptTaskData.ScriptTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.impl.ScriptTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.impl.chunk.ActivityNodeDataImpl;

public class ScriptTaskDataBuilderImpl
    extends ActivityNodeDataBuilderImpl<ScriptTaskDataBuilder, ScriptTaskData, ScriptTaskDataImpl>
    implements ScriptTaskDataBuilder {
  @Override
  protected ScriptTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected ActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ScriptTaskDataImpl createData() {
    return new ScriptTaskDataImpl();
  }
}
