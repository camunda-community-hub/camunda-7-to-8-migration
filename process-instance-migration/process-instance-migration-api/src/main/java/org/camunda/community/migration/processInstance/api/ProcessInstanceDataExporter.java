package org.camunda.community.migration.processInstance.api;

import org.camunda.community.migration.processInstance.api.model.Page;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceMetadata;

public interface ProcessInstanceDataExporter {
  /**
   * Returns a {@link Page} of {@link ProcessInstanceMetadata} for the given bpmnProcessId in the
   * latest version, with the given start/offset and limit/length
   *
   * @param bpmnProcessId the bpmn xml id of the process
   * @param start the page offset
   * @param limit the page length
   * @return a {@link Page} of {@link ProcessInstanceMetadata}
   */
  Page<ProcessInstanceMetadata> page(String bpmnProcessId, int start, int limit);

  /**
   * Returns a single {@link ProcessInstanceData} for the given processInstanceId or null
   *
   * @param processInstanceId the processInstanceId of the process instance
   * @return {@link ProcessInstanceData} or null
   */
  ProcessInstanceData get(String processInstanceId);

  /**
   * Invoked if a process instance migration is started.
   *
   * <p>Here, it should be asserted that no more changes can be applied to the state of the process
   * instance.
   *
   * <p>Is called before the process instance data is fetched (using {@link
   * ProcessInstanceDataExporter#get})
   *
   * @param processInstanceId the processInstanceId of the process instance to migrate
   */
  void onMigrationStarted(String processInstanceId);

  /**
   * Invoked if the process instance was migrated successfully
   *
   * <p>Here, the acknowledgment happens after a process instance was migrated from the old system.
   *
   * @param processInstanceId the processInstanceId of the migrated source process instance
   * @param processInstanceKey the processInstanceKey of the migrated target process instance
   */
  void onMigrationSuccess(String processInstanceId, long processInstanceKey);

  /**
   * Invoked if the process instance migration failed
   *
   * <p>Here, the compensation for a failed process instance migration can take place
   *
   * @param processInstanceId the processInstanceId of the process instance not migrated
   */
  void onMigrationFailed(String processInstanceId);
}
