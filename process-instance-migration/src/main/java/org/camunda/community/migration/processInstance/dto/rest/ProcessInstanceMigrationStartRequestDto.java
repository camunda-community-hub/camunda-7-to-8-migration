package org.camunda.community.migration.processInstance.dto.rest;

public class ProcessInstanceMigrationStartRequestDto {
  private String bpmnProcessId;
  private String migrationType;

  public String getMigrationType() {
    return migrationType;
  }

  public void setMigrationType(String migrationType) {
    this.migrationType = migrationType;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }
}
