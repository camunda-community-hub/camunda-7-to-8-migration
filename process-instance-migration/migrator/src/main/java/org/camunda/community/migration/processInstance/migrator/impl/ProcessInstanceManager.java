package org.camunda.community.migration.processInstance.migrator.impl;

import java.util.List;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationOrder;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceRepository;

public class ProcessInstanceManager {
  private final ProcessInstanceRepository processInstanceRepository;

  public ProcessInstanceManager(ProcessInstanceRepository processInstanceRepository) {
    this.processInstanceRepository = processInstanceRepository;
  }

  public ProcessInstanceMigrationOrder createOrder(Camunda7ProcessInstance processInstanceData) {
    return processInstanceRepository.createOrder(processInstanceData);
  }

  public ProcessInstanceMigrationStatus getMigrationStatus(ProcessInstanceMigrationOrder order) {
    return processInstanceRepository.getMigrationStatus(order);
  }

  public ProcessInstanceMigrationOrder getOrderInStatus(ProcessInstanceMigrationStatus status) {
    return processInstanceRepository.getOrderInStatus(status);
  }

  public void updateToExtracted(
      ProcessInstanceMigrationOrder order, ProcessInstanceData processInstanceData) {
    processInstanceRepository.updateToExtracted(order, processInstanceData);
  }

  public void updateToFailed(ProcessInstanceMigrationOrder order, List<String> messages) {
    processInstanceRepository.updateToFailed(order, messages);
  }

  public ProcessInstanceData getData(ProcessInstanceMigrationOrder order) {
    return processInstanceRepository.getData(order);
  }

  public void updateToStarted(
      ProcessInstanceMigrationOrder order, Camunda8ProcessInstance processInstance) {
    processInstanceRepository.updateToStarted(order, processInstance);
  }

  public Camunda8ProcessInstance getMigratedInstance(ProcessInstanceMigrationOrder order) {
    return processInstanceRepository.getMigratedInstance(order);
  }

  public ProcessInstanceMigrationOrder getOrderForCamunda7ProcessInstance(
      Camunda7ProcessInstance processInstance) {
    return processInstanceRepository.getOrderForCamunda7ProcessInstance(processInstance);
  }

  public Camunda7ProcessInstance getCamunda7ProcessInstanceForOrder(
      ProcessInstanceMigrationOrder order) {
    return processInstanceRepository.getCamunda7ProcessInstanceForOrder(order);
  }

  public void updateToCompleted(
      ProcessInstanceMigrationOrder order, Camunda8ProcessInstance camunda8ProcessInstance) {
    processInstanceRepository.updateToCompleted(order, camunda8ProcessInstance);
  }
}
