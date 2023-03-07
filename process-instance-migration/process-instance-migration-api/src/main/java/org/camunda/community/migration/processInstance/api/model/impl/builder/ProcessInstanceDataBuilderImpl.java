package org.camunda.community.migration.processInstance.api.model.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.impl.builder.BuilderUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData.ProcessInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.impl.ProcessInstanceDataImpl;

public class ProcessInstanceDataBuilderImpl
    extends BasicBuilderImpl<ProcessInstanceData, ProcessInstanceDataImpl>
    implements ProcessInstanceDataBuilder {

  @Override
  public ProcessInstanceDataBuilder bpmnProcessId(String bpmnProcessId) {
    data.setBpmnProcessId(bpmnProcessId);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder id(String id) {
    data.setId(id);
    return this;
  }

  @Override
  protected ProcessInstanceDataImpl createData() {
    return new ProcessInstanceDataImpl();
  }

  @Override
  public ProcessInstanceDataBuilder activity(String activityName, ActivityNodeData activityData) {
    addMapEntry(data::getActivities, data::setActivities, activityName, activityData);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder variable(String variableName, JsonNode variableValue) {
    addMapEntry(data::getVariables, data::setVariables, variableName, variableValue);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder name(String name) {
    data.setName(name);
    return this;
  }
}
