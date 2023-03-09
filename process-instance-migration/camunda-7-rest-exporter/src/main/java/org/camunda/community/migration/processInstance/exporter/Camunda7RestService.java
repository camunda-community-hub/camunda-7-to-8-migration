package org.camunda.community.migration.processInstance.exporter;

import java.util.List;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.api.ProcessInstanceApi;
import org.camunda.community.rest.client.api.VariableInstanceApi;
import org.camunda.community.rest.client.dto.ActivityInstanceDto;
import org.camunda.community.rest.client.dto.ProcessDefinitionDto;
import org.camunda.community.rest.client.dto.ProcessInstanceDto;
import org.camunda.community.rest.client.dto.ProcessInstanceQueryDto;
import org.camunda.community.rest.client.dto.VariableInstanceDto;
import org.camunda.community.rest.client.invoker.ApiException;

public class Camunda7RestService {
  private final ProcessDefinitionApi processDefinitionApi;
  private final ProcessInstanceApi processInstanceApi;
  private final VariableInstanceApi variableInstanceApi;

  public Camunda7RestService(
      ProcessDefinitionApi processDefinitionApi,
      ProcessInstanceApi processInstanceApi,
      VariableInstanceApi variableInstanceApi) {
    this.processDefinitionApi = processDefinitionApi;
    this.processInstanceApi = processInstanceApi;
    this.variableInstanceApi = variableInstanceApi;
  }

  List<VariableInstanceDto> getVariableInstances(String processInstanceId) {
    try {
      return variableInstanceApi.getVariableInstances(
          null,
          null,
          processInstanceId,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  public long getProcessInstancesCount(String processDefinitionId) {
    try {
      return processInstanceApi
          .getProcessInstancesCount(
              null,
              null,
              null,
              null,
              processDefinitionId,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null)
          .getCount();
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  List<ProcessInstanceDto> getProcessInstances(String processDefinitionId, int start, int limit) {
    ProcessInstanceQueryDto query = new ProcessInstanceQueryDto();
    query.setProcessDefinitionId(processDefinitionId);
    try {
      return processInstanceApi.queryProcessInstances(start, limit, query);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  ProcessDefinitionDto getProcessDefinition(String processDefinitionId) {
    try {
      return processDefinitionApi.getProcessDefinition(processDefinitionId);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  ProcessDefinitionDto getProcessDefinitionByBpmnProcessId(String bpmnProcessId) {
    try {
      return processDefinitionApi
          .getProcessDefinitions(
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              bpmnProcessId,
              null,
              null,
              null,
              null,
              null,
              true,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null)
          .stream()
          .findFirst()
          .orElseThrow(
              () ->
                  new RuntimeException(
                      "No process definition found for bpmnProcessId '" + bpmnProcessId + "'"));
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void getCalledProcessInstance(String processInstanceId, String callActivityInstanceId) {}

  public ProcessInstanceDto getProcessInstance(String id) {
    try {
      return processInstanceApi.getProcessInstance(id);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }

  public ActivityInstanceDto getActivityInstance(String processInstanceId) {
    try {
      return processInstanceApi.getActivityInstanceTree(processInstanceId);
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
