package org.camunda.community.migration.processInstance;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.TransitionInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.community.migration.processInstance.client.Camunda7Client;
import org.camunda.community.migration.processInstance.dto.client.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.JobDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.TransitionInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.VariableInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.VersionDto;
import org.camunda.community.migration.processInstance.dto.rest.JobDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class Camunda7EmbeddedClient implements Camunda7Client {
  private final ObjectMapper objectMapper;

  public Camunda7EmbeddedClient(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public List<JobDefinitionDto> getJobDefinitions(
      Camunda7JobType jobType,
      Camunda7JobConfiguration jobConfiguration,
      String processDefinitionId) {
    return processEngine()
        .getManagementService()
        .createJobDefinitionQuery()
        .processDefinitionId(processDefinitionId)
        .jobType(jobType.getName())
        .jobConfiguration(jobConfiguration.getName())
        .list()
        .stream()
        .map(this::mapFrom)
        .collect(Collectors.toList());
  }

  @Override
  public void suspendJobDefinition(String jobDefinitionId, boolean suspended) {
    if (suspended) {
      processEngine().getManagementService().suspendJobDefinitionById(jobDefinitionId, true);
    } else {
      processEngine().getManagementService().activateJobDefinitionById(jobDefinitionId, true);
    }
  }

  @Override
  public VersionDto getVersion() {
    VersionDto versionDto = new VersionDto();
    versionDto.setVersion(processEngine().getClass().getPackage().getImplementationVersion());
    return versionDto;
  }

  @Override
  public void suspendProcessDefinitionById(String processDefinitionId, boolean suspended) {
    if (suspended) {
      processEngine()
          .getRepositoryService()
          .updateProcessDefinitionSuspensionState()
          .byProcessDefinitionId(processDefinitionId)
          .includeProcessInstances(true)
          .suspend();
    } else {
      processEngine()
          .getRepositoryService()
          .updateProcessDefinitionSuspensionState()
          .byProcessDefinitionId(processDefinitionId)
          .includeProcessInstances(true)
          .activate();
    }
  }

  @Override
  public ProcessInstanceDto getProcessInstance(String processInstanceId) {
    return Optional.of(
            processEngine()
                .getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult())
        .map(this::mapFrom)
        .orElse(null);
  }

  @Override
  public ProcessDefinitionDto getProcessDefinition(String processDefinitionId) {
    return Optional.of(
            processEngine()
                .getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult())
        .map(this::mapFrom)
        .orElse(null);
  }

  @Override
  public ActivityInstanceDto getActivityInstances(String processInstanceId) {
    return Optional.of(processEngine().getRuntimeService().getActivityInstance(processInstanceId))
        .map(this::mapFrom)
        .orElse(null);
  }

  @Override
  public void cancelProcessInstance(String processInstanceId) {
    processEngine().getRuntimeService().deleteProcessInstance(processInstanceId, null);
  }

  @Override
  public void setVariable(String processInstanceId, String variableName, Object variableValue) {
    processEngine().getRuntimeService().setVariable(processInstanceId, variableName, variableValue);
  }

  @Override
  public List<ProcessInstanceDto> getProcessInstancesByProcessDefinition(
      String processDefinitionId) {
    return processEngine()
        .getRuntimeService()
        .createProcessInstanceQuery()
        .processDefinitionId(processDefinitionId)
        .list()
        .stream()
        .map(this::mapFrom)
        .collect(Collectors.toList());
  }

  @Override
  public List<ProcessInstanceDto> getProcessInstancesByProcessDefinitionAndActivityIds(
      String processDefinitionId, Collection<String> activityIds) {
    return processEngine()
        .getRuntimeService()
        .createProcessInstanceQuery()
        .processDefinitionId(processDefinitionId)
        .activityIdIn(activityIds.toArray(new String[0]))
        .list()
        .stream()
        .map(this::mapFrom)
        .collect(Collectors.toList());
  }

  @Override
  public ProcessDefinitionDto getLatestProcessDefinitionByKey(String processDefinitionKey) {
    return Optional.of(
            processEngine()
                .getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult())
        .map(this::mapFrom)
        .orElse(null);
  }

  @Override
  public List<VariableInstanceDto> getVariableInstances(String processInstanceId) {
    return processEngine()
        .getRuntimeService()
        .createVariableInstanceQuery()
        .processInstanceIdIn(processInstanceId)
        .list()
        .stream()
        .map(this::mapFrom)
        .collect(Collectors.toList());
  }

  @Override
  public List<JobDto> getJobs(String camunda7ProcessInstanceId) {
    return processEngine()
        .getManagementService()
        .createJobQuery()
        .processInstanceId(camunda7ProcessInstanceId)
        .list()
        .stream()
        .map(this::mapFrom)
        .collect(Collectors.toList());
  }

  private JobDto mapFrom(Job job) {
    JobDto dto = new JobDto();
    dto.setId(job.getId());
    dto.setJobDefinitionId(job.getJobDefinitionId());
    return dto;
  }

  private VariableInstanceDto mapFrom(VariableInstance variableInstance) {
    VariableInstanceDto dto = new VariableInstanceDto();
    dto.setExecutionId(variableInstance.getExecutionId());
    dto.setId(variableInstance.getId());
    dto.setName(variableInstance.getName());
    dto.setProcessDefinitionId(variableInstance.getProcessDefinitionId());
    dto.setProcessInstanceId(variableInstance.getProcessInstanceId());
    dto.setType(variableInstance.getTypeName());
    dto.setValue(objectMapper.valueToTree(variableInstance.getValue()));
    return dto;
  }

  private JobDefinitionDto mapFrom(JobDefinition jobDefinition) {
    JobDefinitionDto dto = new JobDefinitionDto();
    dto.setId(jobDefinition.getId());
    dto.setActivityId(jobDefinition.getActivityId());
    dto.setProcessDefinitionId(jobDefinition.getProcessDefinitionId());
    return dto;
  }

  private ProcessDefinitionDto mapFrom(ProcessDefinition processDefinition) {
    ProcessDefinitionDto dto = new ProcessDefinitionDto();
    dto.setId(processDefinition.getId());
    dto.setKey(processDefinition.getKey());
    return dto;
  }

  private ProcessInstanceDto mapFrom(ProcessInstance processInstance) {
    ProcessInstanceDto dto = new ProcessInstanceDto();
    dto.setBusinessKey(processInstance.getBusinessKey());
    dto.setDefinitionId(processInstance.getProcessDefinitionId());
    dto.setId(processInstance.getId());
    return dto;
  }

  private ActivityInstanceDto mapFrom(ActivityInstance activityInstance) {
    ActivityInstanceDto dto = new ActivityInstanceDto();
    dto.setActivityId(activityInstance.getActivityId());
    dto.setActivityType(activityInstance.getActivityType());
    dto.setId(activityInstance.getId());
    dto.setProcessDefinitionId(activityInstance.getProcessDefinitionId());
    dto.setProcessInstanceId(activityInstance.getProcessInstanceId());
    dto.setChildActivityInstances(
        Arrays.stream(activityInstance.getChildActivityInstances())
            .map(this::mapFrom)
            .collect(Collectors.toList()));
    dto.setChildTransitionInstances(
        Arrays.stream(activityInstance.getChildTransitionInstances())
            .map(this::mapFrom)
            .collect(Collectors.toList()));
    return dto;
  }

  private TransitionInstanceDto mapFrom(TransitionInstance transitionInstance) {
    TransitionInstanceDto dto = new TransitionInstanceDto();
    dto.setActivityId(transitionInstance.getActivityId());
    dto.setActivityType(transitionInstance.getActivityType());
    dto.setId(transitionInstance.getId());
    dto.setProcessDefinitionId(transitionInstance.getProcessDefinitionId());
    dto.setProcessInstanceId(transitionInstance.getProcessInstanceId());
    return dto;
  }
}
