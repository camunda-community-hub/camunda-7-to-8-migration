package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.springframework.stereotype.Component;

@Component
public class ZeebeJobClient {
  private final ZeebeClient zeebeClient;
  private final Camunda7Client camunda7Client;

  public ZeebeJobClient(ZeebeClient zeebeClient, Camunda7Client camunda7Client) {
    this.zeebeClient = zeebeClient;
    this.camunda7Client = camunda7Client;
  }

  @JobWorker(type = "camunda7:suspend")
  public void suspendProcessInstance(
      @VariablesAsType ProcessInstanceMigrationVariables variables) {
    camunda7Client.suspendProcessInstance(variables.getCamunda7ProcessInstanceId());
  }

  @JobWorker(type = "camunda7:extract")
  public void extractProcessData(@VariablesAsType ProcessInstanceMigrationVariables variables){
    camunda7Client.getProcessData(variables.getCamunda7ProcessInstanceId());
  }
}
