package org.camunda.community.migration.processInstance;

import static io.camunda.zeebe.protocol.Protocol.*;

import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.ProcessConstants.JobType;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ActivityData;
import org.camunda.community.migration.processInstance.dto.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceSelectionTask;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceSelectionTask.TaskState;
import org.camunda.community.migration.processInstance.service.Camunda7Service;
import org.camunda.community.migration.processInstance.service.Camunda8Service;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintService;
import org.camunda.community.migration.processInstance.service.ProcessInstanceSelectionService;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZeebeJobClient {
  private static final Logger LOG = LoggerFactory.getLogger(ZeebeJobClient.class);
  private final Camunda7Service camunda7Service;
  private final ProcessInstanceSelectionService selectionService;
  private final Camunda8Service camunda8Service;
  private final ProcessDefinitionMigrationHintService processDefinitionMigrationHintService;

  public ZeebeJobClient(
      Camunda7Service camunda7Service,
      ProcessInstanceSelectionService selectionService,
      Camunda8Service camunda8Service,
      ProcessDefinitionMigrationHintService processDefinitionMigrationHintService) {
    this.camunda7Service = camunda7Service;
    this.selectionService = selectionService;
    this.camunda8Service = camunda8Service;
    this.processDefinitionMigrationHintService = processDefinitionMigrationHintService;
  }

  @JobWorker(type = JobType.CAMUNDA7_SUSPEND)
  public void suspendProcessInstance(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    LOG.info("Suspending process definition '{}'", variables.getCamunda7ProcessDefinitionId());
    camunda7Service.suspendProcessDefinition(variables.getCamunda7ProcessDefinitionId(), true);
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
        "Starting process instance with corresponding c7 instance '{}'",
        variables.getCamunda7ProcessInstanceId());
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
    camunda7Service.cancelProcessInstance(
        variables.getCamunda7ProcessInstanceId(), variables.getCamunda8ProcessInstanceKey());
  }

  @JobWorker(type = JobType.CAMUNDA7_VERSIONED_INFORMATION)
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
      LOG.info("Found selection task {}", job.getKey());
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
