package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.camunda.community.migration.processInstance.core.dto.Camunda7ProcessData;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.springframework.stereotype.Component;

@Component
public class ZeebeJobClient {
  private final ZeebeClient zeebeClient;
  private final Camunda7Service camunda7Service;

  public ZeebeJobClient(ZeebeClient zeebeClient, Camunda7Service camunda7Service) {
    this.zeebeClient = zeebeClient;
    this.camunda7Service = camunda7Service;
  }

  @JobWorker(type = "camunda7:suspend")
  public void suspendProcessInstance(@VariablesAsType ProcessInstanceMigrationVariables variables) {
    camunda7Service.suspendProcessInstance(variables.getCamunda7ProcessInstanceId());
  }

  @JobWorker(type = "camunda7:extract")
  public ProcessInstanceMigrationVariables extractProcessData(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    Camunda7ProcessData processData =
        camunda7Service.getProcessData(variables.getCamunda7ProcessInstanceId());
    variables.setActivityIds(processData.getActivityIds());
    variables.setVariables(processData.getProcessVariables());
    return variables;
  }

  @JobWorker(type = "camunda8:checkProcessDefinition")
  public ProcessInstanceMigrationVariables checkProcessDefinition(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {

  }
}
