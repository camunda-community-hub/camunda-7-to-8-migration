package org.camunda.community.migration.processInstance.service;

import static org.camunda.community.migration.processInstance.ProcessConstants.FormKey.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.rest.ConversionAndDeploymentCreationTaskDataDto;
import org.camunda.community.migration.processInstance.dto.rest.JobDefinitionSelectionTaskDataDto;
import org.camunda.community.migration.processInstance.dto.rest.ProcessInstanceSelectionTaskDataDto;
import org.camunda.community.migration.processInstance.dto.rest.ProcessInstanceSelectionTaskDataDto.ProcessInstanceDataDto;
import org.camunda.community.migration.processInstance.dto.rest.RouteExecutionCancellationTaskDataDto;
import org.camunda.community.migration.processInstance.dto.rest.UserTaskDto.UserTaskDataDto;
import org.camunda.community.migration.processInstance.dto.task.ConversionAndDeploymentCreationTaskData;
import org.camunda.community.migration.processInstance.dto.task.JobDefinitionSelectionTaskData;
import org.camunda.community.migration.processInstance.dto.task.ProcessInstanceSelectionTaskData;
import org.camunda.community.migration.processInstance.dto.task.RouteExecutionCancellationTaskData;
import org.camunda.community.migration.processInstance.dto.task.UserTask.UserTaskData;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskMappingService {
  private static final Map<
          String, BiFunction<ProcessInstanceMigrationVariables, Camunda7Service, UserTaskData>>
      TO_USER_TASK_MAPPING = new HashMap<>();
  private static final Map<String, Function<UserTaskData, UserTaskDataDto>> TO_DTO_MAPPING =
      new HashMap<>();
  private static final Map<String, String> TASK_NAME_MAPPING = new HashMap<>();

  static {
    TASK_NAME_MAPPING.put(SELECT_PROCESS_INSTANCES, "Select Process Instances");
    TASK_NAME_MAPPING.put(CREATE_AND_DEPLOY_CONVERSION, "Create and deploy Conversion");
    TASK_NAME_MAPPING.put(SELECT_JOB_DEFINITION, "Select job definition");
    TASK_NAME_MAPPING.put(CANCEL_ROUTE_EXECUTION, "Cancel route execution");
    TO_USER_TASK_MAPPING.put(
        SELECT_PROCESS_INSTANCES,
        (variables, service) ->
            new ProcessInstanceSelectionTaskData(
                variables.getBpmnProcessId(),
                variables.getCamunda7ProcessDefinitionId(),
                service
                    .getProcessInstancesByProcessDefinitionId(
                        variables.getCamunda7ProcessDefinitionId())
                    .stream()
                    .map(dto -> service.getProcessData(dto.getId()))
                    .collect(Collectors.toList())));
    TO_USER_TASK_MAPPING.put(
        CREATE_AND_DEPLOY_CONVERSION,
        (variables, service) ->
            new ConversionAndDeploymentCreationTaskData(variables.getBpmnProcessId()));
    TO_USER_TASK_MAPPING.put(
        SELECT_JOB_DEFINITION,
        (variables, service) ->
            new JobDefinitionSelectionTaskData(
                variables.getCamunda7ProcessDefinitionId(), variables.getCamunda7JobDefinitions()));
    TO_USER_TASK_MAPPING.put(
        CANCEL_ROUTE_EXECUTION,
        (variables, service) ->
            new RouteExecutionCancellationTaskData(
                variables.getCamunda7ProcessDefinitionId(), variables.getCamunda7JobDefinitions()));
    TO_DTO_MAPPING.put(
        SELECT_PROCESS_INSTANCES,
        (data) -> {
          ProcessInstanceSelectionTaskDataDto dto = new ProcessInstanceSelectionTaskDataDto();
          dto.setBpmnProcessId(data.as(ProcessInstanceSelectionTaskData.class).getBpmnProcessId());
          dto.setAvailableProcessInstances(
              data
                  .as(ProcessInstanceSelectionTaskData.class)
                  .getAvailableProcessInstances()
                  .stream()
                  .map(
                      pi -> {
                        ProcessInstanceDataDto instanceDataDto = new ProcessInstanceDataDto();
                        instanceDataDto.setId(pi.getProcessInstanceId());
                        instanceDataDto.setBusinessKey(pi.getBusinessKey());
                        instanceDataDto.setMigrationHints(pi.getMigrationHints());
                        return instanceDataDto;
                      })
                  .collect(Collectors.toList()));
          return dto;
        });
    TO_DTO_MAPPING.put(
        CREATE_AND_DEPLOY_CONVERSION,
        (data) -> {
          ConversionAndDeploymentCreationTaskDataDto dto =
              new ConversionAndDeploymentCreationTaskDataDto();
          dto.setBpmnProcessId(
              data.as(ConversionAndDeploymentCreationTaskData.class).getBpmnProcessId());
          return dto;
        });
    TO_DTO_MAPPING.put(
        SELECT_JOB_DEFINITION,
        (data) -> {
          JobDefinitionSelectionTaskDataDto dto = new JobDefinitionSelectionTaskDataDto();
          dto.setCamunda7ProcessDefinitionId(
              data.as(JobDefinitionSelectionTaskData.class).getCamunda7ProcessDefinitionId());
          dto.setCamunda7JobDefinitions(
              data.as(JobDefinitionSelectionTaskData.class).getCamunda7JobDefinitions());
          return dto;
        });
    TO_DTO_MAPPING.put(
        CANCEL_ROUTE_EXECUTION,
        (data) -> {
          RouteExecutionCancellationTaskDataDto dto = new RouteExecutionCancellationTaskDataDto();
          dto.setCamunda7ProcessDefinitionId(
              data.as(RouteExecutionCancellationTaskData.class).getCamunda7ProcessDefinitionId());
          dto.setCamunda7JobDefinitions(
              data.as(RouteExecutionCancellationTaskData.class).getCamunda7JobDefinitions());
          return dto;
        });
  }

  private final Camunda7Service camunda7Service;

  @Autowired
  public TaskMappingService(Camunda7Service camunda7Service) {
    this.camunda7Service = camunda7Service;
  }

  public String getTaskName(String type) {
    return TASK_NAME_MAPPING.get(type);
  }

  public Optional<UserTaskData> createUserTaskData(
      String type, ProcessInstanceMigrationVariables variables) {
    return Optional.ofNullable(TO_USER_TASK_MAPPING.get(type))
        .map(mapper -> mapper.apply(variables, camunda7Service));
  }

  public UserTaskDataDto crateDto(String type, UserTaskData data) {
    return Optional.ofNullable(TO_DTO_MAPPING.get(type))
        .map(mapper -> mapper.apply(data))
        .orElseThrow(
            () -> new NullPointerException("No registered Mapper for type '" + type + "' present"));
  }
}
