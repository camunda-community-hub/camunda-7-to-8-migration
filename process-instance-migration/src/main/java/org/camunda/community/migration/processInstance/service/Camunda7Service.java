package org.camunda.community.migration.processInstance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.client.Camunda7Client;
import org.camunda.community.migration.processInstance.dto.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ActivityData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ProcessVariableData;
import org.camunda.community.migration.processInstance.dto.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Camunda7Service {
  private final Camunda7Client camunda7Client;

  @Autowired
  public Camunda7Service(Camunda7Client camunda7Client) {
    this.camunda7Client = camunda7Client;
  }

  public Camunda7ProcessInstanceData getProcessData(String camunda7ProcessInstanceId) {
    Camunda7ProcessInstanceData processData = new Camunda7ProcessInstanceData();
    processData.setProcessInstanceId(camunda7ProcessInstanceId);
    // process definition key
    ProcessInstanceDto processInstance =
        camunda7Client.getProcessInstance(camunda7ProcessInstanceId);
    ProcessDefinitionDto processDefinition =
        camunda7Client.getProcessDefinition(processInstance.getDefinitionId());
    processData.setProcessDefinitionKey(processDefinition.getKey());
    // variables
    Map<String, ProcessVariableData> variables = new HashMap<>();
    JsonNode businessKey =
        processInstance.getBusinessKey() == null
            ? NullNode.getInstance()
            : TextNode.valueOf(processInstance.getBusinessKey());
    variables.put("businessKey", ProcessVariableData.of(businessKey, camunda7ProcessInstanceId));

    camunda7Client
        .getVariableInstances(camunda7ProcessInstanceId)
        .forEach(
            (variable) ->
                variables.put(
                    variable.getName(),
                    ProcessVariableData.of(variable.getValue(), variable.getExecutionId())));
    processData.setProcessVariables(variables);
    // activity ids
    ActivityInstanceDto activities = camunda7Client.getActivityInstances(camunda7ProcessInstanceId);
    processData.setActivities(extractFromTree(activities));
    return processData;
  }

  private List<ActivityData> extractFromTree(ActivityInstanceDto activityInstanceDto) {
    ActivityData data = new ActivityData();
    data.setId(activityInstanceDto.getActivityId());
    data.setType(activityInstanceDto.getActivityType());
    if (activityInstanceDto.getChildActivityInstances() == null
        || activityInstanceDto.getChildActivityInstances().isEmpty()) {
      return Collections.singletonList(data);
    } else {
      List<ActivityData> result =
          activityInstanceDto.getChildActivityInstances().stream()
              .flatMap(dto -> extractFromTree(dto).stream())
              .collect(Collectors.toList());
      if (!data.getType().equals("processDefinition")) {
        result.add(data);
      }
      return result;
    }
  }

  public void suspendProcessDefinition(String processDefinitionId, boolean suspended) {
    camunda7Client.suspendProcessDefinitionById(processDefinitionId, suspended);
  }

  public void cancelProcessInstance(
      String camunda7ProcessInstanceId, Long camunda8ProcessInstanceKey) {
    camunda7Client.setVariable(
        camunda7ProcessInstanceId, "camunda8ProcessInstanceKey", camunda8ProcessInstanceKey);
    camunda7Client.cancelProcessInstance(camunda7ProcessInstanceId);
  }

  public List<ProcessInstanceDto> getProcessInstancesByProcessDefinitionId(
      String processDefinitionId) {
    return camunda7Client.getProcessInstancesByProcessDefinition(processDefinitionId);
  }

  public ProcessDefinitionDto getLatestProcessDefinition(String bpmnProcessId) {
    List<ProcessDefinitionDto> processDefinitionByKey =
        camunda7Client.getLatestProcessDefinitionByKey(bpmnProcessId);
    return processDefinitionByKey.isEmpty() ? null : processDefinitionByKey.get(0);
  }
}
