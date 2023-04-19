package org.camunda.community.migration.processInstance.exporter.api;

import java.util.List;

public interface ProcessEngineService {
  ProcessDefinition getLatestProcessDefinition(String bpmnProcessId);

  ProcessDefinition getProcessDefinition(String processDefinitionId);

  long getProcessInstanceCount(String processDefinitionId);

  List<ProcessInstance> getProcessInstances(String processDefinitionId, int offset, int limit);

  ProcessInstance getProcessInstance(String processInstanceId);

  ActivityInstance getActivityInstance(String processInstanceId);

  List<ProcessInstance> getProcessInstancesBySuperProcessInstanceId(String superProcessInstanceId);

  List<VariableInstance> getVariableInstances(String activityInstanceId);

  void setVariable(String processInstanceId, String variableName, Long variableValue);

  void suspendProcessInstance(String processInstanceId);

  void deleteProcessInstance(String processInstanceId, String reason);

  void activateProcessInstance(String processInstanceId);

  boolean isTransitionExecuted(String executionId);
}
