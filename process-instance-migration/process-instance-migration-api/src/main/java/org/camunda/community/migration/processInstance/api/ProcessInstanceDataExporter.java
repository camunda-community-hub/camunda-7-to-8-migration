package org.camunda.community.migration.processInstance.api;

import org.camunda.community.migration.processInstance.api.model.ProcessInstanceData;

public interface ProcessInstanceDataExporter {
  /**
   * @param start
   * @param limit
   * @return
   */
  Page<ProcessInstanceData> list(long start, long limit);

  ProcessInstanceData get(String id);

  void onMigrationStarted(String id);

  void onMigrationSuccess(String id);

  void onMigrationFailed(String id);
}
