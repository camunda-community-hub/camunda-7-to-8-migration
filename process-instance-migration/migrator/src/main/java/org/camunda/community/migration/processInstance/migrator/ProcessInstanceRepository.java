package org.camunda.community.migration.processInstance.migrator;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationOrder;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus;

public interface ProcessInstanceRepository {
  ProcessInstanceMigrationOrder createOrder(Camunda7ProcessInstance processInstance);

  ProcessInstanceMigrationStatus getMigrationStatus(
      ProcessInstanceMigrationOrder processInstanceId);

  ProcessInstanceMigrationOrder getOrderInStatus(ProcessInstanceMigrationStatus status);

  void updateToExtracted(
      ProcessInstanceMigrationOrder order, ProcessInstanceData processInstanceData);

  void updateToFailed(ProcessInstanceMigrationOrder order, List<String> messages);

  ProcessInstanceData getData(ProcessInstanceMigrationOrder order);

  void updateToStarted(
      ProcessInstanceMigrationOrder order, Camunda8ProcessInstance processInstance);

  Camunda8ProcessInstance getMigratedInstance(ProcessInstanceMigrationOrder order);

  ProcessInstanceMigrationOrder getOrderForCamunda7ProcessInstance(
      Camunda7ProcessInstance processInstance);

  Camunda7ProcessInstance getCamunda7ProcessInstanceForOrder(ProcessInstanceMigrationOrder order);

  void updateToCompleted(
      ProcessInstanceMigrationOrder order, Camunda8ProcessInstance camunda8ProcessInstance);
}
