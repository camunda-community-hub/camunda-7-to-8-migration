package org.camunda.community.migration.processInstance;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.camunda.community.migration.processInstance.ProcessConstants.ErrorCode.*;

import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.ProcessConstants.JobType;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessDefinitionData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ActivityData;
import org.camunda.community.migration.processInstance.dto.client.JobDefinitionDto;
import org.camunda.community.migration.processInstance.dto.task.UserTask;
import org.camunda.community.migration.processInstance.dto.task.UserTask.TaskState;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.MigrationTaskService;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintService;
import org.camunda.community.migration.processInstance.service.TaskMappingService;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZeebeJobClient {
  private static final Logger LOG = LoggerFactory.getLogger(ZeebeJobClient.class);
  private final Camunda7Service camunda7Service;
  private final MigrationTaskService migrationTaskService;
  private final Camunda8Service camunda8Service;
  private final ProcessDefinitionMigrationHintService processDefinitionMigrationHintService;
  private final TaskMappingService taskMappingService;

  public ZeebeJobClient(
      Camunda7Service camunda7Service,
      MigrationTaskService migrationTaskService,
      Camunda8Service camunda8Service,
      ProcessDefinitionMigrationHintService processDefinitionMigrationHintService,
      TaskMappingService taskMappingService) {
    this.camunda7Service = camunda7Service;
    this.migrationTaskService = migrationTaskService;
    this.camunda8Service = camunda8Service;
    this.processDefinitionMigrationHintService = processDefinitionMigrationHintService;
    this.taskMappingService = taskMappingService;
  }

  @JobWorker(type = JobType.CAMUNDA7_SUSPEND)
  public void suspendProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Suspending process definition '{}'", variables.getCamunda7ProcessDefinitionId());
    camunda7Service.suspendProcessDefinition(variables.getCamunda7ProcessDefinitionId(), true);
  }

  @JobWorker(type = JobType.CAMUNDA7_SUSPEND_JOB)
  public void suspendJob(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    Map<String, String> selectedJobDefinitions = variables.getSelectedJobDefinitions();
    LOG.info("Suspending job definitions {}", selectedJobDefinitions);
    camunda7Service.suspendJobDefinitions(selectedJobDefinitions.keySet());
  }

  @JobWorker(type = JobType.CAMUNDA7_CONTINUE_JOB)
  public void continueJob(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    Map<String, String> selectedJobDefinitions = variables.getSelectedJobDefinitions();
    LOG.info("Continuing job definitions {}", selectedJobDefinitions);
    camunda7Service.continueJobDefinitions(selectedJobDefinitions.keySet());
  }

  @JobWorker(type = JobType.CAMUNDA7_QUERY_ROUTABLE_INSTANCES)
  public ProcessInstanceMigrationVariables queryRoutableInstances(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    variables.setCamunda7ProcessInstanceIds(
        camunda7Service
            .getProcessInstancesByProcessDefinitionIdAndExclusiveActivityIds(
                variables.getCamunda7ProcessDefinitionId(),
                variables.getSelectedJobDefinitions().values())
            .stream()
            .map(Camunda7ProcessInstanceData::getProcessInstanceId)
            .collect(Collectors.toList()));
    LOG.info("Found process instances to migrate: {}", variables.getCamunda7ProcessInstanceIds());
    return variables;
  }

  @JobWorker(type = JobType.CAMUNDA7_EXTRACT)
  public ProcessInstanceMigrationVariables extractProcessData(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Extracting process data from '{}'", variables.getCamunda7ProcessInstanceId());
    Camunda7ProcessInstanceData processData =
        camunda7Service.getProcessData(variables.getCamunda7ProcessInstanceId());
    LOG.info("Returning activity ids '{}'", processData.getActivities());
    variables.setActivityIds(
        processData.getActivities().stream().map(ActivityData::getId).collect(Collectors.toList()));
    variables.setVariables(
        processData.getProcessVariables().entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getValue())));
    LOG.info("Returning variables '{}'", processData.getProcessVariables());
    return variables;
  }

  @JobWorker(type = JobType.CAMUNDA8_CHECK_PROCESS_DEFINITION)
  public ProcessInstanceMigrationVariables checkProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Checking if process definition '{}' already exists", variables.getBpmnProcessId());
    SearchQuery query = new SearchQuery();
    ProcessDefinitionFilter filter = new ProcessDefinitionFilter();
    filter.setBpmnProcessId(variables.getBpmnProcessId());
    query.setFilter(filter);
    Optional<ProcessDefinition> processDefinition =
        camunda8Service.queryProcessDefinitions(variables.getBpmnProcessId()).stream()
            .max(Comparator.comparingLong(ProcessDefinition::getVersion));
    boolean alreadyConverted = processDefinition.isPresent();
    processDefinition.ifPresent(
        pd ->
            variables.setConversionHints(
                processDefinitionMigrationHintService.getMigrationHints(
                    camunda8Service.getProcessDefinitionData(pd.getKey()))));
    LOG.info("Found {} process definition", processDefinition.isPresent() ? "a" : "no");
    variables.setAlreadyConverted(alreadyConverted);
    return variables;
  }

  @JobWorker(type = JobType.CAMUNDA8_START)
  public ProcessInstanceMigrationVariables startCamunda8ProcessInstance(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    Map<String, Object> processVariables = new HashMap<>(variables.getVariables());
    processVariables.put("camunda7ProcessInstanceId", variables.getCamunda7ProcessInstanceId());
    LOG.info(
        "Starting process instance with corresponding c7 instance '{}' at {}",
        variables.getCamunda7ProcessInstanceId(),
        variables.getActivityIds());
    ProcessInstanceEvent processInstanceEvent =
        camunda8Service.startMigratedProcessInstance(
            variables.getBpmnProcessId(), variables.getActivityIds(), processVariables);
    LOG.info(
        "Started process instance with key '{}'", processInstanceEvent.getProcessInstanceKey());
    variables.setCamunda8ProcessInstanceKey(processInstanceEvent.getProcessInstanceKey());
    return variables;
  }

  @JobWorker(type = JobType.CAMUNDA7_CONTINUE)
  public void continueProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Continuing process definition '{}'", variables.getBpmnProcessId());
    camunda7Service.suspendProcessDefinition(variables.getCamunda7ProcessDefinitionId(), false);
  }

  @JobWorker(type = JobType.CAMUNDA7_CANCEL)
  public void cancelProcessInstance(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Canceling process instance '{}'", variables.getCamunda7ProcessInstanceId());
    try {
      camunda7Service.cancelProcessInstance(
          variables.getCamunda7ProcessInstanceId(), variables.getCamunda8ProcessInstanceKey());
    } catch (Exception e) {
      throw new ZeebeBpmnError(
          CANCEL_PROCESS_INSTANCE,
          "Error while cancelling process instance " + variables.getCamunda7ProcessInstanceId());
    }
  }

  @JobWorker(type = JobType.CAMUNDA8_CANCEL)
  public void cancelCamunda8ProcessInstance(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    camunda8Service.cancelProcessInstance(variables.getCamunda8ProcessInstanceKey());
  }

  @JobWorker(type = JobType.CAMUNDA7_VERSIONED_INFORMATION)
  public ProcessInstanceMigrationVariables extractVersionInformation(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    Camunda7ProcessDefinitionData data =
        camunda7Service.getLatestProcessDefinition(variables.getBpmnProcessId());
    variables.setCamunda7ProcessDefinitionId(data.getProcessDefinition().getId());

    variables.setCamunda7JobDefinitions(
        data.getJobDefinitions().stream()
            .collect(Collectors.toMap(JobDefinitionDto::getId, JobDefinitionDto::getActivityId)));
    return variables;
  }

  @JobWorker(
      type = USER_TASK_JOB_TYPE,
      timeout = 1000L,
      autoComplete = false,
      requestTimeout = 1000L)
  public void userTask(
      ActivatedJob job, @VariablesAsType ProcessInstanceMigrationVariables variables) {
    String formKey = job.getCustomHeaders().get(USER_TASK_FORM_KEY_HEADER_NAME);
    taskMappingService
        .createUserTaskData(formKey, variables)
        .ifPresent(
            data ->
                migrationTaskService.addTask(
                    new UserTask(
                        job.getKey(),
                        taskMappingService.getTaskName(formKey),
                        job.getProcessInstanceKey(),
                        formKey,
                        data,
                        TaskState.CREATED)));
  }
}
