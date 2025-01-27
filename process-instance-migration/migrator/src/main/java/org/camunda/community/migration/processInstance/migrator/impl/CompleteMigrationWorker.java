package org.camunda.community.migration.processInstance.migrator.impl;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.util.List;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstanceState;
import org.camunda.community.migration.processInstance.importer.InitialVariableContext;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrationSteps;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;

public class CompleteMigrationWorker implements JobHandler {
  private final ProcessInstanceMigrationSteps steps;

  public CompleteMigrationWorker(ProcessInstanceMigrationSteps steps) {
    this.steps = steps;
  }

  @Override
  public void handle(JobClient client, ActivatedJob job) {
    InitialVariableContext variables = job.getVariablesAsType(InitialVariableContext.class);
    steps.updateToCompleted(
        new Camunda7ProcessInstance(variables.camunda7ProcessInstanceId()),
        new Camunda8ProcessInstanceState(List.of(job.getElementInstanceKey())));
  }
}
