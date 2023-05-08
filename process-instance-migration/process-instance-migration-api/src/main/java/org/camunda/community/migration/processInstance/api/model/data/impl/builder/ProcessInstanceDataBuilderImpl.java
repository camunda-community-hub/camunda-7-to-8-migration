package org.camunda.community.migration.processInstance.api.model.data.impl.builder;

import static org.camunda.community.migration.processInstance.api.model.data.impl.builder.BuilderUtil.*;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.FinalBuildStep.FinalBuildStepImpl;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData.ProcessInstanceDataBuilder;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.impl.ProcessInstanceDataImpl;

public class ProcessInstanceDataBuilderImpl
    extends FinalBuildStepImpl<ProcessInstanceData, ProcessInstanceDataImpl>
    implements ProcessInstanceDataBuilder {

  @Override
  public ProcessInstanceDataBuilder withBpmnProcessId(String bpmnProcessId) {
    data.setBpmnProcessId(bpmnProcessId);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder withProcessDefinitionKey(String processDefinitionKey) {
    data.setProcessDefinitionKey(processDefinitionKey);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder withId(String id) {
    data.setId(id);
    return this;
  }

  @Override
  protected ProcessInstanceDataImpl createData() {
    return new ProcessInstanceDataImpl();
  }

  @Override
  public ProcessInstanceDataBuilder withActivity(
      String activityName, ActivityNodeData activityData) {
    addMapEntry(data::getActivities, data::setActivities, activityName, activityData);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder withActivities(Map<String, ActivityNodeData> activities) {
    activities.forEach(this::withActivity);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder withVariable(String variableName, JsonNode variableValue) {
    addMapEntry(data::getVariables, data::setVariables, variableName, variableValue);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder withVariables(Map<String, JsonNode> variables) {
    variables.forEach(this::withVariable);
    return this;
  }

  @Override
  public ProcessInstanceDataBuilder withName(String name) {
    data.setName(name);
    return this;
  }
}
