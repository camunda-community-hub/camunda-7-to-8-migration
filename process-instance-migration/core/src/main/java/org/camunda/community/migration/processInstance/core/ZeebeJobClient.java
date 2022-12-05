package org.camunda.community.migration.processInstance.core;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.camunda.community.migration.processInstance.core.dto.Camunda7ProcessData;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.camunda.community.migration.processInstance.core.ProcessConstants.JobType.*;

@Component
public class ZeebeJobClient {
  private final ZeebeClient zeebeClient;
  private final CamundaOperateClient operateClient;
  private final Camunda7Service camunda7Service;

  public ZeebeJobClient(
      ZeebeClient zeebeClient,
      CamundaOperateClient operateClient,
      Camunda7Service camunda7Service) {
    this.zeebeClient = zeebeClient;
    this.operateClient = operateClient;
    this.camunda7Service = camunda7Service;
  }

  @JobWorker(type = CAMUNDA7_SUSPEND)
  public void suspendProcessInstance(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    camunda7Service.suspendProcessDefinition(variables.getBpmnProcessId(), true);
  }

  @JobWorker(type = CAMUNDA7_EXTRACT)
  public ProcessInstanceMigrationVariables extractProcessData(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    Camunda7ProcessData processData =
        camunda7Service.getProcessData(variables.getCamunda7ProcessInstanceId());
    variables.setActivityIds(processData.getActivityIds());
    variables.setVariables(processData.getProcessVariables());
    return variables;
  }

  @JobWorker(type = CAMUNDA8_CHECK_PROCESS_DEFINITION)
  public ProcessInstanceMigrationVariables checkProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) throws OperateException {
    SearchQuery query = new SearchQuery();
    ProcessDefinitionFilter filter = new ProcessDefinitionFilter();
    filter.setBpmnProcessId(variables.getBpmnProcessId());
    query.setFilter(filter);
    List<ProcessDefinition> processDefinitions = operateClient.searchProcessDefinitions(query);
    // TODO maybe download and compare old and new process definition for mapping
    variables.setAlreadyConverted(!processDefinitions.isEmpty());
    return variables;
  }

  @JobWorker(type = CAMUNDA8_START)
  public ProcessInstanceMigrationVariables startCamunda8ProcessInstance(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    CreateProcessInstanceCommandStep3 command =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId(variables.getBpmnProcessId())
            .latestVersion()
            .variables(variables.getVariables());
    variables.getActivityIds().forEach(command::startBeforeElement);
    ProcessInstanceEvent processInstanceEvent = command.send().join();
    variables.setCamunda8ProcessInstanceKey(processInstanceEvent.getProcessInstanceKey());
    return variables;
  }

  @JobWorker(type = CAMUNDA7_CONTINUE)
  public void continueProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    camunda7Service.suspendProcessDefinition(variables.getBpmnProcessId(), false);
  }
}
