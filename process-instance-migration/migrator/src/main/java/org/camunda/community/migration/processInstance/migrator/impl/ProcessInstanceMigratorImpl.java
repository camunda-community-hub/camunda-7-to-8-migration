package org.camunda.community.migration.processInstance.migrator.impl;

import java.util.List;
import org.camunda.community.migration.processInstance.api.ProcessInstanceDataExporter;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstanceState;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrationSteps;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Created;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Extracted;

public class ProcessInstanceMigratorImpl
    implements ProcessInstanceMigrator, ProcessInstanceMigrationSteps {
  private final ProcessInstanceDataExporter processInstanceDataExporter;
  private final Camunda8ProcessInstanceImporter processInstanceDataImporter;
  private final ProcessInstanceManager processInstanceManager;

  public ProcessInstanceMigratorImpl(
      ProcessInstanceDataExporter processInstanceDataExporter,
      Camunda8ProcessInstanceImporter processInstanceDataImporter,
      ProcessInstanceManager processInstanceManager) {
    this.processInstanceDataExporter = processInstanceDataExporter;
    this.processInstanceDataImporter = processInstanceDataImporter;
    this.processInstanceManager = processInstanceManager;
  }

  @Override
  public ProcessInstanceMigrationOrder prepareMigration(Camunda7ProcessInstance processInstance) {
    return processInstanceManager.createOrder(processInstance);
  }

  @Override
  public ProcessInstanceMigrationStatus checkMigrationStatus(ProcessInstanceMigrationOrder order) {
    return processInstanceManager.getMigrationStatus(order);
  }

  @Override
  public void updateToExtracted() {
    ProcessInstanceMigrationOrder order = processInstanceManager.getOrderInStatus(new Created());
    if (order == null) {
      return;
    }
    Camunda7ProcessInstance processInstance =
        processInstanceManager.getCamunda7ProcessInstanceForOrder(order);
    try {
      ProcessInstanceData processInstanceData =
          processInstanceDataExporter.get(processInstance.processInstanceId());
      processInstanceManager.updateToExtracted(order, processInstanceData);
    } catch (Exception e) {
      processInstanceManager.updateToFailed(order, List.of(e.getMessage()));
    }
  }

  @Override
  public void updateToStarted() {
    ProcessInstanceMigrationOrder order = processInstanceManager.getOrderInStatus(new Extracted());
    if (order == null) {
      return;
    }
    try {
      ProcessInstanceData data = processInstanceManager.getData(order);
      Camunda8ProcessInstanceImporter.Camunda8ProcessInstance processInstance =
          processInstanceDataImporter.createProcessInstance(data);
      processInstanceManager.updateToStarted(order, processInstance);
    } catch (Exception e) {
      processInstanceManager.updateToFailed(order, List.of(e.getMessage()));
    }
  }

  @Override
  public void updateToCompleted(
      Camunda7ProcessInstance processInstance, Camunda8ProcessInstanceState stateToTerminate) {
    ProcessInstanceMigrationOrder order =
        processInstanceManager.getOrderForCamunda7ProcessInstance(processInstance);
    ProcessInstanceData data = processInstanceManager.getData(order);
    Camunda8ProcessInstanceImporter.Camunda8ProcessInstance camunda8ProcessInstance =
        processInstanceManager.getMigratedInstance(order);
    processInstanceDataImporter.updateProcessInstanceToStatus(
        camunda8ProcessInstance, data, stateToTerminate);
    processInstanceManager.updateToCompleted(order, camunda8ProcessInstance);
  }
}
