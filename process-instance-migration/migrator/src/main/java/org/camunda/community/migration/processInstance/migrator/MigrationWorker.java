package org.camunda.community.migration.processInstance.migrator;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.camunda.community.migration.processInstance.migrator.impl.CompleteMigrationWorker;
import org.springframework.stereotype.Component;

@Component
public class MigrationWorker {
  private final CompleteMigrationWorker completeMigrationWorker;

  public MigrationWorker(ProcessInstanceMigrationSteps steps) {
    completeMigrationWorker = new CompleteMigrationWorker(steps);
  }

  @JobWorker
  public void completeMigration(JobClient client, ActivatedJob job) {
    completeMigrationWorker.handle(client, job);
  }
}
