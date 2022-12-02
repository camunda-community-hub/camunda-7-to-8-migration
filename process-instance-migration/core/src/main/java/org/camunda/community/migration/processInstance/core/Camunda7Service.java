package org.camunda.community.migration.processInstance.core;

import org.camunda.community.migration.processInstance.core.dto.ActivityInstanceDto;
import org.camunda.community.migration.processInstance.core.dto.Camunda7ProcessData;
import org.camunda.community.migration.processInstance.core.dto.ProcessDefinitionDto;
import org.camunda.community.migration.processInstance.core.dto.ProcessInstanceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Camunda7Service {
  private final Camunda7Client camunda7Client;

  @Autowired
  public Camunda7Service(Camunda7Client camunda7Client) {
    this.camunda7Client = camunda7Client;
  }

  public Camunda7ProcessData getProcessData(String camunda7ProcessInstanceId) {
    Camunda7ProcessData processData = new Camunda7ProcessData();
    // process definition key
    ProcessInstanceDto processInstance =
        camunda7Client.getProcessInstance(camunda7ProcessInstanceId);
    ProcessDefinitionDto processDefinition =
        camunda7Client.getProcessDefinition(processInstance.getDefinitionId());
    processData.setProcessDefinitionKey(processDefinition.getKey());
    // variables
    Map<String, Object> variables = camunda7Client.getVariables(camunda7ProcessInstanceId);
    processData.setProcessVariables(variables);
    // activity ids
    ActivityInstanceDto activities = camunda7Client.getActivityInstances(camunda7ProcessInstanceId);
    return processData;
  }
}
