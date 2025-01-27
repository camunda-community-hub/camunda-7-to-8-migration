package org.camunda.community.migration.processInstance.migrator;

import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstanceState;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;

public interface ProcessInstanceMigrationSteps {
  /** Updates the status of a process instance to extracted */
  void updateToExtracted();

  /** Updates the status of a process instance to started */
  void updateToStarted();

  /**
   * Updates the status of the process instance completed
   *
   * @param processInstance that should be completed
   */
  void updateToCompleted(
      Camunda7ProcessInstance processInstance, Camunda8ProcessInstanceState stateToTerminate);
}
