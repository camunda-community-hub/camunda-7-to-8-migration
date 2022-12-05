package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceMigrationStarter {
  private final ZeebeClient zeebeClient;

  @Autowired
  public ProcessInstanceMigrationStarter(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  public ProcessInstanceEvent startProcessInstanceMigration(String bpmnProcessId) {
    ProcessInstanceMigrationVariables variables = new ProcessInstanceMigrationVariables();
    variables.setBpmnProcessId(bpmnProcessId);
    return zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId("ProcessInstanceMigrationProcess")
        .latestVersion()
        .variables(variables)
        .send()
        .join();
  }
}
