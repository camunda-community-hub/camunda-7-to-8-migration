package org.camunda.community.migration.processInstance.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.camunda.community.migration.processInstance.dto.client.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.HistoricActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.HistoricActivityInstanceDto.HistoricActivityInstanceQueryResultDto;
import org.camunda.community.migration.processInstance.dto.client.HistoricVariableInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.HistoricVariableInstanceDto.HistoricVariableInstanceQueryResultDto;
import org.camunda.community.migration.processInstance.dto.client.JobDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.JobDefinitionDto.JobDefinitionQueryResultDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessDefinitionDto.ProcessDefinitionQueryResultDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessInstanceDto.ProcessInstanceQueryResultDto;
import org.camunda.community.migration.processInstance.dto.client.VariableInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.VariableInstanceDto.VariableInstanceQueryResultDto;
import org.camunda.community.migration.processInstance.dto.client.VariableValueDto;
import org.camunda.community.migration.processInstance.dto.client.VersionDto;
import org.camunda.community.migration.processInstance.dto.rest.JobDto;
import org.camunda.community.migration.processInstance.dto.rest.JobDto.JobQueryResultDto;
import org.camunda.community.migration.processInstance.properties.Camunda7ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Camunda7Client {
  private static final Logger LOG = LoggerFactory.getLogger(Camunda7Client.class);
  private static final String BY_ID = "/{id}";
  private static final String SUSPENDED = "/suspended";
  private static final String PROCESS_DEFINITION = "/process-definition";
  private static final String PROCESS_INSTANCE = "/process-instance";
  private static final String VARIABLE_INSTANCE = "/variable-instance";
  private static final String JOB_DEFINITION = "/job-definition";
  private static final String JOB = "/job";

  private static final String PROCESS_DEFINITION_BY_ID = PROCESS_DEFINITION + BY_ID;
  private static final String PROCESS_DEFINITION_SUSPENDED = PROCESS_DEFINITION_BY_ID + SUSPENDED;
  private static final String PROCESS_INSTANCE_BY_ID = PROCESS_INSTANCE + BY_ID;
  private static final String PROCESS_INSTANCE_VARIABLES = PROCESS_INSTANCE_BY_ID + "/variables";
  private static final String PROCESS_INSTANCE_VARIABLE = PROCESS_INSTANCE_VARIABLES + "/{varName}";
  private static final String PROCESS_INSTANCE_ACTIVITY_INSTANCES =
      PROCESS_INSTANCE_BY_ID + "/activity-instances";
  private static final String VERSION = "/version";
  private static final String HISTORY = "/history";
  private static final String HISTORIC_ACTIVITY_INSTANCE = HISTORY + "/activity-instance";
  private static final String HISTORIC_VARIABLE_INSTANCE = HISTORY + "/variable-instance";
  private static final String JOB_DEFINITION_BY_ID = JOB_DEFINITION + BY_ID;
  private static final String JOB_DEFINITION_SUSPENDED = JOB_DEFINITION_BY_ID + SUSPENDED;
  private final RestTemplate restTemplate;
  private final Camunda7ClientProperties properties;

  public Camunda7Client(RestTemplate restTemplate, Camunda7ClientProperties properties) {
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  @PostConstruct
  public void testConnection() {
    if (properties.getCheckOnInit()) {
      try {
        VersionDto version = getVersion();
        LOG.info("Connected to Camunda 7 version {}", version.getVersion());
      } catch (Exception e) {
        throw new RuntimeException("Could not establish a connection to Camunda 7", e);
      }
    }
  }

  private String buildQuery(Set<String> keys) {
    return "?" + keys.stream().map(key -> key + "={" + key + "}").collect(Collectors.joining("&"));
  }

  public List<JobDefinitionDto> getJobDefinitions(
      Camunda7JobType jobType,
      Camunda7JobConfiguration jobConfiguration,
      String processDefinitionId) {
    Map<String, String> variables = new HashMap<>();
    variables.put("jobType", jobType.getName());
    variables.put("jobConfiguration", jobConfiguration.getName());
    variables.put("processDefinitionId", processDefinitionId);
    return restTemplate.getForObject(
        JOB_DEFINITION + buildQuery(variables.keySet()),
        JobDefinitionQueryResultDto.class,
        variables);
  }

  public void suspendJobDefinition(String jobDefinitionId, boolean suspended) {
    Map<String, Object> body = new HashMap<>();
    body.put("suspended", suspended);
    body.put("includeJobs", true);
    restTemplate.put(
        JOB_DEFINITION_SUSPENDED, body, Collections.singletonMap("id", jobDefinitionId));
  }

  public VersionDto getVersion() {
    return restTemplate.getForObject(VERSION, VersionDto.class);
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
        PROCESS_INSTANCE_BY_ID,
        ProcessInstanceDto.class,
        Collections.singletonMap("id", processInstanceId));
  }

  public ProcessDefinitionDto getProcessDefinition(String processDefinitionId) {
    return restTemplate.getForObject(
        PROCESS_DEFINITION_BY_ID,
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
        PROCESS_INSTANCE_ACTIVITY_INSTANCES,
        ActivityInstanceDto.class,
        Collections.singletonMap("id", processInstanceId));
  }

  public List<HistoricActivityInstanceDto> getHistoricActivityInstances(String processInstanceId) {
    Map<String, String> parameters =
        Collections.singletonMap("processInstanceId", processInstanceId);
    return restTemplate.getForObject(
        HISTORIC_ACTIVITY_INSTANCE + buildQuery(parameters.keySet()),
        HistoricActivityInstanceQueryResultDto.class,
        parameters);
  }

  public void cancelProcessInstance(String processInstanceId) {
    restTemplate.delete(PROCESS_INSTANCE_BY_ID, Collections.singletonMap("id", processInstanceId));
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
    Map<String, String> parameters =
        Collections.singletonMap("processDefinitionId", processDefinitionId);
    return restTemplate.getForObject(
        PROCESS_INSTANCE + buildQuery(parameters.keySet()),
        ProcessInstanceQueryResultDto.class,
        parameters);
  }

  public List<ProcessInstanceDto> getProcessInstancesByProcessDefinitionAndActivityIds(
      String processDefinitionId, Collection<String> activityIds) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("processDefinitionId", processDefinitionId);
    parameters.put("activityIdIn", String.join(",", activityIds));
    return restTemplate.getForObject(
        PROCESS_INSTANCE + buildQuery(parameters.keySet()),
        ProcessInstanceQueryResultDto.class,
        parameters);
  }

  public List<ProcessDefinitionDto> getLatestProcessDefinitionByKey(String processDefinitionKey) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("latestVersion", Boolean.TRUE.toString());
    parameters.put("key", processDefinitionKey);
    return restTemplate.getForObject(
        PROCESS_DEFINITION + buildQuery(parameters.keySet()),
        ProcessDefinitionQueryResultDto.class,
        parameters);
  }

  public List<ProcessInstanceDto> getProcessInstances() {
    return restTemplate.getForObject(PROCESS_INSTANCE, ProcessInstanceQueryResultDto.class);
  }

  public List<HistoricVariableInstanceDto> getHistoricVariables(String processInstanceId) {
    Map<String, String> parameters =
        Collections.singletonMap("processInstanceId", processInstanceId);
    return restTemplate.getForObject(
        HISTORIC_VARIABLE_INSTANCE + buildQuery(parameters.keySet()),
        HistoricVariableInstanceQueryResultDto.class,
        parameters);
  }

  public List<VariableInstanceDto> getVariableInstances(String processInstanceId) {
    Map<String, String> parameters =
        Collections.singletonMap("processInstanceIdIn", processInstanceId);
    return restTemplate.getForObject(
        VARIABLE_INSTANCE + buildQuery(parameters.keySet()),
        VariableInstanceQueryResultDto.class,
        parameters);
  }

  public List<JobDto> getJobs(String camunda7ProcessInstanceId) {
    Map<String, String> variables = new HashMap<>();
    variables.put("processInstanceId", camunda7ProcessInstanceId);
    return restTemplate.getForObject(
        JOB + buildQuery(variables.keySet()), JobQueryResultDto.class, variables);
  }

  public enum Camunda7JobType {
    ASYNC_CONTINUATION("async-continuation");
    private final String name;

    Camunda7JobType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public enum Camunda7JobConfiguration {
    ASYNC_BEFORE("async-before");
    private final String name;

    Camunda7JobConfiguration(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
