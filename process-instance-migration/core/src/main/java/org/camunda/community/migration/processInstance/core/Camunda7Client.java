package org.camunda.community.migration.processInstance.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.community.migration.processInstance.core.dto.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.core.dto.Camunda7VersionDto;
import org.camunda.community.migration.processInstance.core.dto.HistoricActivityInstanceDto;
import org.camunda.community.migration.processInstance.core.dto.HistoricActivityInstanceDto.HistoricActivityInstanceQueryResultDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessDefinitionDto.ProcessDefinitionQueryResultDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessInstanceDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessInstanceDto.ProcessInstanceQueryResultDto;
import org.camunda.community.migration.processInstance.core.dto.VariableValueDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Camunda7Client {
  private static final String PROCESS_INSTANCE = "/process-instance/{id}";
  private static final String PROCESS_INSTANCE_VARIABLES = PROCESS_INSTANCE + "/variables";
  private static final String ACTIVITY_INSTANCES = PROCESS_INSTANCE + "/activity-instances";
  private static final String PROCESS_DEFINITION = "/process-definition/{id}";
  private static final String PROCESS_DEFINITION_LATEST_BY_KEY =
      "/process-definition?latestVersion=true&processDefinitionKey={processDefinitionKey}";
  private static final String VERSION = "/version";
  private static final String PROCESS_DEFINITION_SUSPENDED = "/process-definition/{id}/suspended";
  private static final String HISTORY = "/history";
  private static final String HISTORIC_ACTIVITY_INSTANCE =
      HISTORY + "/activity-instance?processInstanceId={processInstanceId}";
  private static final String PROCESS_INSTANCE_VARIABLE =
      "/process-instance/{id}/variables/{varName}";
  private static final String PROCESS_INSTANCE_LIST =
      "/process-instance?processDefinitionId={processDefinitionId}";
  private final RestTemplate restTemplate;

  public Camunda7Client(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Camunda7VersionDto getVersion() {
    return restTemplate.getForObject(VERSION, Camunda7VersionDto.class);
  }

  public void suspendProcessDefinitionById(String processDefinitionId, boolean suspended) {
    Map<String, Object> body = new HashMap<>();
    body.put("suspended", suspended);
    body.put("includeProcessInstances", true);
    restTemplate.put(
        PROCESS_DEFINITION_SUSPENDED, body, Collections.singletonMap("id", processDefinitionId));
  }

  public ProcessInstanceDto getProcessInstance(String processInstanceId) {
    return restTemplate.getForObject(
        PROCESS_INSTANCE,
        ProcessInstanceDto.class,
        Collections.singletonMap("id", processInstanceId));
  }

  public ProcessDefinitionDto getProcessDefinition(String processDefinitionId) {
    return restTemplate.getForObject(
        PROCESS_DEFINITION,
        ProcessDefinitionDto.class,
        Collections.singletonMap("id", processDefinitionId));
  }

  public Map<String, VariableValueDto> getVariables(String processInstanceId) {
    return restTemplate
        .exchange(
            PROCESS_INSTANCE_VARIABLES,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, VariableValueDto>>() {},
            Collections.singletonMap("id", processInstanceId))
        .getBody();
  }

  public ActivityInstanceDto getActivityInstances(String processInstanceId) {
    return restTemplate.getForObject(
        ACTIVITY_INSTANCES,
        ActivityInstanceDto.class,
        Collections.singletonMap("id", processInstanceId));
  }

  public List<HistoricActivityInstanceDto> getHistoricActivityInstances(String processInstanceId) {
    return restTemplate.getForObject(
        HISTORIC_ACTIVITY_INSTANCE,
        HistoricActivityInstanceQueryResultDto.class,
        Collections.singletonMap("processInstanceId", processInstanceId));
  }

  public void cancelProcessInstance(String processInstanceId) {
    restTemplate.delete(PROCESS_INSTANCE, Collections.singletonMap("id", processInstanceId));
  }

  public void setVariable(String processInstanceId, String variableName, Object variableValue) {
    Map<String, Object> body = new HashMap<>();
    body.put("value", variableValue);
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("id", processInstanceId);
    uriVariables.put("varName", variableName);
    restTemplate.put(PROCESS_INSTANCE_VARIABLE, body, uriVariables);
  }

  public List<ProcessInstanceDto> getProcessInstancesByProcessDefinition(
      String processDefinitionId) {
    return restTemplate.getForObject(
        PROCESS_INSTANCE_LIST,
        ProcessInstanceQueryResultDto.class,
        Collections.singletonMap("processDefinitionId", processDefinitionId));
  }

  public List<ProcessDefinitionDto> getLatestProcessDefinitionByKey(String processDefinitionKey) {
    return restTemplate.getForObject(
        PROCESS_DEFINITION_LATEST_BY_KEY,
        ProcessDefinitionQueryResultDto.class,
        Collections.singletonMap("processDefinitionKey", processDefinitionKey));
  }
}
