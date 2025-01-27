package org.camunda.community.migration.processInstance.migrator.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.camunda.community.migration.processInstance.api.model.data.ProcessInstanceData;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter.Camunda8ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.Camunda7ProcessInstance;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationOrder;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Completed;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Created;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Extracted;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Failed;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceMigrator.ProcessInstanceMigrationStatus.Started;
import org.camunda.community.migration.processInstance.migrator.ProcessInstanceRepository;

public class InMemoryProcessInstanceRepository implements ProcessInstanceRepository {
  private final Map<String, ProcessInstanceRecord> data;

  InMemoryProcessInstanceRepository(Map<String, ProcessInstanceRecord> data) {
    this.data = data;
  }

  public InMemoryProcessInstanceRepository() {
    this(new ConcurrentHashMap<>());
  }

  @Override
  public ProcessInstanceMigrationOrder createOrder(Camunda7ProcessInstance processInstance) {
    String id = UUID.randomUUID().toString();
    ProcessInstanceRecord processInstanceRecord = new ProcessInstanceRecord();
    processInstanceRecord.setCamunda7ProcessInstance(processInstance);
    processInstanceRecord.setMigrationStatus(new Created());
    data.put(id, processInstanceRecord);
    return new ProcessInstanceMigrationOrder(id);
  }

  @Override
  public ProcessInstanceMigrationStatus getMigrationStatus(
      ProcessInstanceMigrationOrder processInstanceId) {
    return data.get(processInstanceId.reference()).getMigrationStatus();
  }

  @Override
  public ProcessInstanceMigrationOrder getOrderInStatus(ProcessInstanceMigrationStatus status) {
    return data.entrySet().stream()
        .filter(entry -> entry.getValue().getMigrationStatus().equals(status))
        .map(e -> new ProcessInstanceMigrationOrder(e.getKey()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void updateToExtracted(
      ProcessInstanceMigrationOrder order, ProcessInstanceData processInstanceData) {
    verifyOrderExists(order);
    ProcessInstanceRecord processInstanceRecord = data.get(order.reference());
    processInstanceRecord.setProcessInstanceData(processInstanceData);
    processInstanceRecord.setMigrationStatus(new Extracted());
  }

  private void verifyOrderExists(ProcessInstanceMigrationOrder order) {
    if (!data.containsKey(order.reference())) {
      throw new NullPointerException(
          "No process instance record found for id " + order.reference());
    }
  }

  @Override
  public void updateToFailed(ProcessInstanceMigrationOrder order, List<String> messages) {
    verifyOrderExists(order);
    ProcessInstanceRecord processInstanceRecord = data.get(order.reference());
    processInstanceRecord.setMigrationStatus(new Failed(messages));
  }

  @Override
  public ProcessInstanceData getData(ProcessInstanceMigrationOrder order) {
    verifyOrderExists(order);
    ProcessInstanceRecord processInstanceRecord = data.get(order.reference());
    return processInstanceRecord.getProcessInstanceData();
  }

  @Override
  public void updateToStarted(
      ProcessInstanceMigrationOrder order, Camunda8ProcessInstance processInstance) {
    verifyOrderExists(order);
    ProcessInstanceRecord processInstanceRecord = data.get(order.reference());
    processInstanceRecord.setMigrationStatus(new Started(processInstance.processInstanceKey()));
  }

  @Override
  public void updateToCompleted(
      ProcessInstanceMigrationOrder order, Camunda8ProcessInstance processInstance) {
    verifyOrderExists(order);
    ProcessInstanceRecord processInstanceRecord = data.get(order.reference());
    processInstanceRecord.setMigrationStatus(new Completed(processInstance.processInstanceKey()));
  }

  @Override
  public Camunda8ProcessInstance getMigratedInstance(ProcessInstanceMigrationOrder order) {
    verifyOrderExists(order);
    ProcessInstanceRecord processInstanceRecord = data.get(order.reference());
    ProcessInstanceMigrationStatus migrationStatus = processInstanceRecord.getMigrationStatus();
    if (migrationStatus instanceof Started started) {
      return new Camunda8ProcessInstance(started.processInstanceKey());
    }
    throw new IllegalStateException(
        "Expected process instance to be in state 'Started' but was '"
            + migrationStatus.getClass().getSimpleName()
            + "'");
  }

  @Override
  public ProcessInstanceMigrationOrder getOrderForCamunda7ProcessInstance(
      Camunda7ProcessInstance processInstance) {
    return data.entrySet().stream()
        .filter(e -> e.getValue().getCamunda7ProcessInstance().equals(processInstance))
        .map(e -> new ProcessInstanceMigrationOrder(e.getKey()))
        .findFirst()
        .orElseThrow(
            () ->
                new NullPointerException("No order found for process instance " + processInstance));
  }

  @Override
  public Camunda7ProcessInstance getCamunda7ProcessInstanceForOrder(
      ProcessInstanceMigrationOrder order) {
    verifyOrderExists(order);
    return data.get(order.reference()).getCamunda7ProcessInstance();
  }

  private static class ProcessInstanceRecord {
    private Camunda7ProcessInstance camunda7ProcessInstance;
    private ProcessInstanceMigrationStatus migrationStatus;
    private ProcessInstanceData processInstanceData;

    public ProcessInstanceMigrationStatus getMigrationStatus() {
      return migrationStatus;
    }

    public void setMigrationStatus(ProcessInstanceMigrationStatus migrationStatus) {
      this.migrationStatus = migrationStatus;
    }

    public ProcessInstanceData getProcessInstanceData() {
      return processInstanceData;
    }

    public void setProcessInstanceData(ProcessInstanceData processInstanceData) {
      this.processInstanceData = processInstanceData;
    }

    public Camunda7ProcessInstance getCamunda7ProcessInstance() {
      return camunda7ProcessInstance;
    }

    public void setCamunda7ProcessInstance(Camunda7ProcessInstance camunda7ProcessInstance) {
      this.camunda7ProcessInstance = camunda7ProcessInstance;
    }
  }
}
