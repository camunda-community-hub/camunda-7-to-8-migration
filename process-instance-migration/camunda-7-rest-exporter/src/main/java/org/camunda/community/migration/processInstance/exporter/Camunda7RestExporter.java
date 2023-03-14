package org.camunda.community.migration.processInstance.exporter;

import static org.camunda.community.migration.processInstance.api.model.data.Builder.*;
import static org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.api.ProcessInstanceDataExporter;
import org.camunda.community.migration.processInstance.api.model.Page;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData;
import org.camunda.community.migration.processInstance.api.model.data.chunk.ActivityNodeData.ActivityNodeDataBuilder;
import org.camunda.community.rest.client.dto.ActivityInstanceDto;
import org.camunda.community.rest.client.dto.ProcessDefinitionDto;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.VariableInstanceDto;

public class Camunda7RestExporter implements ProcessInstanceDataExporter {
  private final Camunda7RestService camunda7RestService;
  private final ObjectMapper objectMapper;

  public Camunda7RestExporter(Camunda7RestService camunda7RestService, ObjectMapper objectMapper) {
    this.camunda7RestService = camunda7RestService;
    this.objectMapper = objectMapper;
  }

  @Override
  public Page<ProcessInstanceMetadata> page(String bpmnProcessId, int start, int limit) {
    ProcessDefinitionDto processDefinition =
        camunda7RestService.getProcessDefinition(bpmnProcessId);
    return Page.<ProcessInstanceMetadata>builder()
        .withLimit(limit)
        .withStart(start)
        .withTotal(camunda7RestService.getProcessInstancesCount(processDefinition.getId()))
        .withResults(
            camunda7RestService
                .getProcessInstances(processDefinition.getId(), start, limit)
                .stream()
                .map(pi -> extractData(pi, processDefinition))
                .collect(Collectors.toList()))
        .build();
  }

  private ProcessInstanceMetadata extractData(
      ProcessInstanceDto processInstanceDto, ProcessDefinitionDto processDefinitionDto) {
    return processInstanceMetadata()
        .withBpmnProcessId(processDefinitionDto.getKey())
        .withName(processDefinitionDto.getName())
        .withProcessDefinitionKey(processDefinitionDto.getId())
        .withId(processInstanceDto.getId())
        .build();
  }

  @Override
  public ProcessInstanceData get(String processInstanceId) {
    ProcessInstanceDto processInstanceDto =
        camunda7RestService.getProcessInstance(processInstanceId);
    ProcessDefinitionDto processDefinitionDto =
        camunda7RestService.getProcessDefinition(processInstanceDto.getDefinitionId());
    ActivityInstanceDto activityInstanceDto =
        camunda7RestService.getActivityInstance(processInstanceId);

    List<VariableInstanceDto> variableInstanceDtoList =
        camunda7RestService.getVariableInstances(processInstanceId);
    return extractData(
        processInstanceDto, processDefinitionDto, activityInstanceDto, variableInstanceDtoList);
  }

  private ProcessInstanceData extractData(
      ProcessInstanceDto processInstanceDto,
      ProcessDefinitionDto processDefinitionDto,
      ActivityInstanceDto activityInstanceDto,
      List<VariableInstanceDto> variableInstanceDtoList) {
    return processInstanceData()
        .withBpmnProcessId(processDefinitionDto.getKey())
        .withName(processDefinitionDto.getName())
        .withProcessDefinitionKey(processDefinitionDto.getId())
        .withId(processInstanceDto.getId())
        .withActivities(
            extractActivities(
                activityInstanceDto.getChildActivityInstances(), variableInstanceDtoList))
        .withVariables(extractVariables(variableInstanceDtoList, activityInstanceDto))
        .build();
  }

  private Map<String, JsonNode> extractVariables(
      List<VariableInstanceDto> variableInstanceDtoList, ActivityInstanceDto activityInstanceDto) {
    if (variableInstanceDtoList == null) {
      return Collections.emptyMap();
    }
    return variableInstanceDtoList.stream()
        .filter(
            variableInstanceDto ->
                Objects.equals(
                    variableInstanceDto.getActivityInstanceId(), activityInstanceDto.getId()))
        .collect(
            Collectors.toMap(
                VariableInstanceDto::getName, v -> objectMapper.valueToTree(v.getValue())));
  }

  private Map<String, ActivityNodeData> extractActivities(
      List<ActivityInstanceDto> childActivityInstances,
      List<VariableInstanceDto> variableInstanceDtoList) {
    if (childActivityInstances == null) {
      return Collections.emptyMap();
    }
    return childActivityInstances.stream()
        .collect(
            Collectors.toMap(
                this::getActivityNodeDataKey, a -> extractActivity(a, variableInstanceDtoList)));
  }

  private String getActivityNodeDataKey(ActivityInstanceDto activityInstanceDto) {
    return activityInstanceDto.getActivityId().replace("#multiInstanceBody", "");
  }

  private ActivityNodeData extractActivity(
      ActivityInstanceDto activityInstanceDto, List<VariableInstanceDto> variableInstanceDtoList) {
    switch (activityInstanceDto.getActivityType()) {
      case "multiInstanceBody":
        // TODO create a multi instance body from here
        return multiInstanceData().build();
      case USER_TASK:
        return decorate(userTaskData(), activityInstanceDto, variableInstanceDtoList).build();
      case SERVICE_TASK:
        return decorate(serviceTaskData(), activityInstanceDto, variableInstanceDtoList).build();
      case SCRIPT_TASK:
        return decorate(scriptTaskData(), activityInstanceDto, variableInstanceDtoList).build();
      case RECEIVE_TASK:
        return decorate(receiveTaskData(), activityInstanceDto, variableInstanceDtoList).build();
      case SEND_TASK:
        return decorate(sendTaskData(), activityInstanceDto, variableInstanceDtoList).build();
      case BUSINESS_RULE_TASK:
        return decorate(businessRuleTaskData(), activityInstanceDto, variableInstanceDtoList)
            .build();
      case CALL_ACTIVITY:
        // TODO append the called process instance here
        return decorate(callActivityData(), activityInstanceDto, variableInstanceDtoList).build();
      case TASK:
        return decorate(taskData(), activityInstanceDto, variableInstanceDtoList).build();
      case MANUAL_TASK:
        return decorate(manualTaskData(), activityInstanceDto, variableInstanceDtoList).build();
      case SUB_PROCESS:
        return decorate(subProcessData(), activityInstanceDto, variableInstanceDtoList)
            .withActivities(
                extractActivities(
                    activityInstanceDto.getChildActivityInstances(), variableInstanceDtoList))
            .build();
      default:
        throw new RuntimeException(
            "No handler defined for activity type '" + activityInstanceDto.getActivityType() + "'");
    }
  }

  private <T extends ActivityNodeDataBuilder<T, ?>> T decorate(
      T builder,
      ActivityInstanceDto activityInstanceDto,
      List<VariableInstanceDto> variableInstanceDtoList) {
    return builder
        .withName(activityInstanceDto.getActivityName())
        .withVariables(extractVariables(variableInstanceDtoList, activityInstanceDto));
  }

  @Override
  public void onMigrationStarted(String processInstanceId) {
    // TODO suspend process instance
  }

  @Override
  public void onMigrationSuccess(String processInstanceId, long processInstanceKey) {
    // TODO eventually cancel process instance
  }

  @Override
  public void onMigrationFailed(String processInstanceId) {
    // TODO continue process instance
  }
}
