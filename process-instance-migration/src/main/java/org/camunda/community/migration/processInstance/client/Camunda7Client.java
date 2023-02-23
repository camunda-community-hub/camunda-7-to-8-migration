package org.camunda.community.migration.processInstance.client;

import java.util.Collection;
import java.util.List;
import org.camunda.community.migration.processInstance.dto.client.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.JobDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.dto.client.ProcessInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.VariableInstanceDto;
import org.camunda.community.migration.processInstance.dto.client.VersionDto;
import org.camunda.community.migration.processInstance.dto.rest.JobDto;

public interface Camunda7Client {
  String BEAN_NAME = "camunda7Client";

  List<JobDefinitionDto> getJobDefinitions(
      Camunda7JobType jobType,
      Camunda7JobConfiguration jobConfiguration,
      String processDefinitionId);

  void suspendJobDefinition(String jobDefinitionId, boolean suspended);

  VersionDto getVersion();

  void suspendProcessDefinitionById(String processDefinitionId, boolean suspended);

  ProcessInstanceDto getProcessInstance(String processInstanceId);

  ProcessDefinitionDto getProcessDefinition(String processDefinitionId);

  ActivityInstanceDto getActivityInstances(String processInstanceId);

  void cancelProcessInstance(String processInstanceId);

  void setVariable(String processInstanceId, String variableName, Object variableValue);

  List<ProcessInstanceDto> getProcessInstancesByProcessDefinition(String processDefinitionId);

  List<ProcessInstanceDto> getProcessInstancesByProcessDefinitionAndActivityIds(
      String processDefinitionId, Collection<String> activityIds);

  ProcessDefinitionDto getLatestProcessDefinitionByKey(String processDefinitionKey);

  List<VariableInstanceDto> getVariableInstances(String processInstanceId);

  List<JobDto> getJobs(String camunda7ProcessInstanceId);

  enum Camunda7JobType {
    ASYNC_CONTINUATION("async-continuation");
    private final String name;

    Camunda7JobType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  enum Camunda7JobConfiguration {
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
