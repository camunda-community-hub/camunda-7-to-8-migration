package org.camunda.community.migration.processInstance.core;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.camunda.community.migration.processInstance.core.ProcessConstants.JobType.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.community.migration.processInstance.core.ProcessInstanceSelectionTask.TaskState;
import org.camunda.community.migration.processInstance.core.dto.Camunda7ProcessData;
import org.camunda.community.migration.processInstance.core.dto.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZeebeJobClient {
  private static final Logger LOG = LoggerFactory.getLogger(ZeebeJobClient.class);
  private final ZeebeClient zeebeClient;
  private final CamundaOperateClient operateClient;
  private final Camunda7Service camunda7Service;
  private final ProcessInstanceSelectionService selectionService;

  public ZeebeJobClient(
      ZeebeClient zeebeClient,
      CamundaOperateClient operateClient,
      Camunda7Service camunda7Service,
      ProcessInstanceSelectionService selectionService) {
    this.zeebeClient = zeebeClient;
    this.operateClient = operateClient;
    this.camunda7Service = camunda7Service;
    this.selectionService = selectionService;
  }

  @JobWorker(type = CAMUNDA7_SUSPEND)
  public void suspendProcessInstance(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Suspending process definition '{}'", variables.getCamunda7ProcessDefinitionId());
    camunda7Service.suspendProcessDefinition(variables.getCamunda7ProcessDefinitionId(), true);
  }

  @JobWorker(type = CAMUNDA7_EXTRACT)
  public ProcessInstanceMigrationVariables extractProcessData(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Extracting process data from '{}'", variables.getCamunda7ProcessInstanceId());
    Camunda7ProcessData processData =
        camunda7Service.getProcessData(variables.getCamunda7ProcessInstanceId());
    LOG.info("Returning activity ids '{}'", processData.getActivityIds());
    variables.setActivityIds(processData.getActivityIds());
    LOG.info("Returning variables '{}'", processData.getProcessVariables());
    variables.setVariables(processData.getProcessVariables());
    return variables;
  }

  @JobWorker(type = CAMUNDA8_CHECK_PROCESS_DEFINITION)
  public ProcessInstanceMigrationVariables checkProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) throws OperateException {
    LOG.info("Checking if process definition '{}' already exists", variables.getBpmnProcessId());
    SearchQuery query = new SearchQuery();
    ProcessDefinitionFilter filter = new ProcessDefinitionFilter();
    filter.setBpmnProcessId(variables.getBpmnProcessId());
    query.setFilter(filter);
    List<ProcessDefinition> processDefinitions = operateClient.searchProcessDefinitions(query);
    boolean alreadyConverted = !processDefinitions.isEmpty();
    LOG.info(
        "Found {} process definitions, returning {}", processDefinitions.size(), alreadyConverted);
    // TODO maybe download and compare old and new process definition for mapping
    variables.setAlreadyConverted(!processDefinitions.isEmpty());
    return variables;
  }

  @JobWorker(type = CAMUNDA8_START)
  public ProcessInstanceMigrationVariables startCamunda8ProcessInstance(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    Map<String, Object> processVariables = new HashMap<>(variables.getVariables());
    processVariables.put("camunda7ProcessInstanceId", variables.getCamunda7ProcessInstanceId());
    LOG.info(
        "Starting process instance with corresponding c7 instance '{}'",
        variables.getCamunda7ProcessInstanceId());
    CreateProcessInstanceCommandStep3 command =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId(variables.getBpmnProcessId())
            .latestVersion()
            .variables(processVariables);
    variables.getActivityIds().forEach(command::startBeforeElement);
    ProcessInstanceEvent processInstanceEvent = command.send().join();
    LOG.info(
        "Started process instance with key '{}'", processInstanceEvent.getProcessInstanceKey());
    variables.setCamunda8ProcessInstanceKey(processInstanceEvent.getProcessInstanceKey());
    return variables;
  }

  @JobWorker(type = CAMUNDA7_CONTINUE)
  public void continueProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Continuing process definition '{}'", variables.getBpmnProcessId());
    camunda7Service.suspendProcessDefinition(variables.getCamunda7ProcessDefinitionId(), false);
  }

  @JobWorker(type = CAMUNDA7_CANCEL)
  public void cancelProcessInstance(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Canceling process instance '{}'", variables.getCamunda7ProcessInstanceId());
    camunda7Service.cancelProcessInstance(
        variables.getCamunda7ProcessInstanceId(), variables.getCamunda8ProcessInstanceKey());
  }

  @JobWorker(type = CAMUNDA7_VERSIONED_INFORMATION)
  public ProcessInstanceMigrationVariables extractVersionInformation(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    ProcessDefinitionDto latestProcessDefinition =
        camunda7Service.getLatestProcessDefinition(variables.getBpmnProcessId());
    variables.setCamunda7ProcessDefinitionId(latestProcessDefinition.getId());
    return variables;
  }

  @JobWorker(type = USER_TASK_JOB_TYPE, timeout = 1000L, autoComplete = false)
  public void userTask(
      ActivatedJob job, @VariablesAsType ProcessInstanceMigrationVariables variables) {
    if (job.getBpmnProcessId().equals("ProcessInstanceMigrationProcess")
        && job.getElementId().equals("SelectProcessInstancesToMigrateTask")) {
      selectionService.addTask(
          new ProcessInstanceSelectionTask(
              job.getKey(),
              job.getProcessInstanceKey(),
              variables.getBpmnProcessId(),
              variables.getCamunda7ProcessDefinitionId(),
              TaskState.CREATED));
    }
  }
}
