package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import org.camunda.community.migration.processInstance.api.model.data.ScriptTaskData;
import org.camunda.community.migration.processInstance.api.model.data.ScriptTaskData.ScriptTaskDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.impl.ScriptTaskDataImpl;
import org.camunda.community.migration.processInstance.api.model.data.impl.chunk.CommonActivityNodeDataImpl;

public class ScriptTaskDataBuilderImpl
    extends CommonActivityNodeDataBuilderImpl<
        ScriptTaskDataBuilder, ScriptTaskData, ScriptTaskDataImpl>
    implements ScriptTaskDataBuilder {
  @Override
  protected ScriptTaskDataBuilder builder() {
    return this;
  }

  @Override
  protected CommonActivityNodeDataImpl data() {
    return data;
  }

  @Override
  protected ScriptTaskDataImpl createData() {
    return new ScriptTaskDataImpl();
  }
}
