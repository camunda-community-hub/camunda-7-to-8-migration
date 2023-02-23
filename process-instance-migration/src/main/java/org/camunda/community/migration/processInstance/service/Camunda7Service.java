package org.camunda.community.migration.processInstance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.client.Camunda7Client;
import org.camunda.community.migration.processInstance.client.Camunda7Client.Camunda7JobConfiguration;
import org.camunda.community.migration.processInstance.client.Camunda7Client.Camunda7JobType;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessDefinitionData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ActivityData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.JobData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ProcessVariableData;
import org.camunda.community.migration.processInstance.dto.client.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessInstanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Camunda7Service {
  private final Camunda7Client camunda7Client;
  private final Set<ProcessInstanceMigrationHintRule> processInstanceMigrationHintRules;

  @Autowired
  public Camunda7Service(
      Camunda7Client camunda7Client,
      Set<ProcessInstanceMigrationHintRule> processInstanceMigrationHintRules) {
    this.camunda7Client = camunda7Client;
    this.processInstanceMigrationHintRules = processInstanceMigrationHintRules;
  }

  private List<String> getMigrationHints(Camunda7ProcessInstanceData processData) {
    return processInstanceMigrationHintRules.stream()
        .map(rule -> rule.createHint(processData))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
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
    processData.setMigrationHints(getMigrationHints(processData));
    processData.setBusinessKey(processInstance.getBusinessKey());
    processData.setJobData(getJobs(camunda7ProcessInstanceId));
    return processData;
  }

  private List<JobData> getJobs(String camunda7ProcessInstanceId) {
    return camunda7Client.getJobs(camunda7ProcessInstanceId).stream()
        .map(
            dto -> {
              JobData data = new JobData();
              data.setJobDefinitionId(dto.getJobDefinitionId());
              data.setId(dto.getId());
              return data;
            })
        .collect(Collectors.toList());
  }

  private List<ActivityData> extractFromTree(ActivityInstanceDto activityInstanceDto) {
    ActivityData data = new ActivityData();
    data.setId(activityInstanceDto.getActivityId());
    data.setType(activityInstanceDto.getActivityType());
    data.setLeaf(true);
    List<ActivityData> result = new ArrayList<>();
    if (!data.getType().equals("processDefinition")) {
      result.add(data);
    }
    if (activityInstanceDto.getChildActivityInstances() != null
        && !activityInstanceDto.getChildActivityInstances().isEmpty()) {
      data.setLeaf(false);
      result.addAll(
          activityInstanceDto.getChildActivityInstances().stream()
              .flatMap(dto -> extractFromTree(dto).stream())
              .collect(Collectors.toList()));
    }
    if (activityInstanceDto.getChildTransitionInstances() != null
        && !activityInstanceDto.getChildTransitionInstances().isEmpty()) {
      data.setLeaf(false);
      result.addAll(
          activityInstanceDto.getChildTransitionInstances().stream()
              .map(
                  transitionInstanceDto -> {
                    ActivityData transitionData = new ActivityData();
                    transitionData.setId(transitionInstanceDto.getActivityId());
                    transitionData.setType(transitionInstanceDto.getActivityType());
                    transitionData.setLeaf(true);
                    return transitionData;
                  })
              .collect(Collectors.toList()));
    }
    return result;
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

  public Camunda7ProcessDefinitionData getLatestProcessDefinition(String bpmnProcessId) {
    Camunda7ProcessDefinitionData data = new Camunda7ProcessDefinitionData();
    ProcessDefinitionDto processDefinitionByKey =
        camunda7Client.getLatestProcessDefinitionByKey(bpmnProcessId);
    data.setProcessDefinition(processDefinitionByKey);
    if (data.getProcessDefinition() != null) {
      data.setJobDefinitions(
          camunda7Client.getJobDefinitions(
              Camunda7JobType.ASYNC_CONTINUATION,
              Camunda7JobConfiguration.ASYNC_BEFORE,
              data.getProcessDefinition().getId()));
    }
    return data;
  }

  public void suspendJobDefinitions(Set<String> selectedJobDefinitions) {
    selectedJobDefinitions.forEach(jobId -> camunda7Client.suspendJobDefinition(jobId, true));
  }

  public List<Camunda7ProcessInstanceData>
      getProcessInstancesByProcessDefinitionIdAndExclusiveActivityIds(
          String camunda7ProcessDefinitionId, Collection<String> activityIds) {
    return camunda7Client
        .getProcessInstancesByProcessDefinitionAndActivityIds(
            camunda7ProcessDefinitionId, activityIds)
        .stream()
        .map(pi -> getProcessData(pi.getId()))
        .filter(
            pi ->
                pi.getActivities().stream()
                    .filter(ActivityData::getLeaf)
                    .map(ActivityData::getId)
                    .allMatch(activityIds::contains))
        .collect(Collectors.toList());
  }

  public void continueJobDefinitions(Set<String> selectedJobDefinitions) {
    selectedJobDefinitions.forEach(jobId -> camunda7Client.suspendJobDefinition(jobId, false));
  }
}
