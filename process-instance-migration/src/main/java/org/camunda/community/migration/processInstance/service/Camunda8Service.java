package org.camunda.community.migration.processInstance.service;

import static org.camunda.community.migration.processInstance.ProcessConstants.BpmnProcessId.*;
import static org.camunda.community.migration.processInstance.ProcessConstants.Message.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.CreateProcessInstanceCommandStep1.CreateProcessInstanceCommandStep3;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.model.bpmn.Bpmn;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.community.migration.processInstance.dto.Camunda8ProcessDefinitionData;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Camunda8Service {
  private final ZeebeClient zeebeClient;
  private final CamundaOperateClient operateClient;

  @Autowired
  public Camunda8Service(ZeebeClient zeebeClient, CamundaOperateClient operateClient) {
    this.zeebeClient = zeebeClient;
    this.operateClient = operateClient;
  }

  public PublishMessageResponse startProcessInstanceMigration(String bpmnProcessId) {
    ProcessInstanceMigrationVariables variables = new ProcessInstanceMigrationVariables();
    variables.setBpmnProcessId(bpmnProcessId);
    return zeebeClient
        .newPublishMessageCommand()
        .messageName(START)
        .correlationKey(bpmnProcessId)
        .timeToLive(Duration.ofSeconds(3))
        .variables(variables)
        .send()
        .join();
  }

  public ProcessInstanceEvent startProcessInstanceMigrationRouter(String bpmnProcessId) {
    ProcessInstanceMigrationVariables variables = new ProcessInstanceMigrationVariables();
    variables.setBpmnProcessId(bpmnProcessId);
    return zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId(ROUTER_PROCESS)
        .latestVersion()
        .variables(variables)
        .send()
        .join();
  }

  public ProcessInstanceEvent startMigratedProcessInstance(
      String bpmnProcessId, List<String> activityIds, Map<String, Object> variables) {
    CreateProcessInstanceCommandStep3 command =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId(bpmnProcessId)
            .latestVersion()
            .variables(variables);
    activityIds.forEach(command::startBeforeElement);
    return command.send().join();
  }

  public void completeTask(long jobKey, ProcessInstanceMigrationVariables result) {
    zeebeClient.newCompleteCommand(jobKey).variables(result).send().join();
  }

  public void selectProcessInstances(long jobKey, List<String> processInstances) {
    zeebeClient
        .newCompleteCommand(jobKey)
        .variables(Collections.singletonMap("camunda7ProcessInstanceIds", processInstances))
        .send()
        .join();
  }

  public List<ProcessDefinition> queryProcessDefinitions(String bpmnProcessId) {
    ProcessDefinitionFilter filter = new ProcessDefinitionFilter();
    filter.setBpmnProcessId(bpmnProcessId);
    SearchQuery query = new SearchQuery();
    query.setFilter(filter);
    try {
      return Optional.ofNullable(operateClient.searchProcessDefinitions(query))
          .orElseGet(ArrayList::new);
    } catch (OperateException e) {
      throw new RuntimeException(e);
    }
  }

  public Camunda8ProcessDefinitionData getProcessDefinitionData(Long processDefinitionKey) {
    Camunda8ProcessDefinitionData data = new Camunda8ProcessDefinitionData();
    try {
      data.setProcessDefinition(operateClient.getProcessDefinition(processDefinitionKey));
      data.setBpmnModelInstance(
          Bpmn.readModelFromStream(
              new ByteArrayInputStream(
                  operateClient.getProcessDefinitionXml(processDefinitionKey).getBytes())));
      return data;
    } catch (OperateException e) {
      throw new RuntimeException(e);
    }
  }

  public void cancelProcessInstance(Long processInstanceKey) {
    zeebeClient.newCancelInstanceCommand(processInstanceKey).send().join();
  }
}
