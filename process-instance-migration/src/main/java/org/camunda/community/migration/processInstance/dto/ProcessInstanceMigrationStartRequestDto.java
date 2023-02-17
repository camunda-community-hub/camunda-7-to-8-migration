package org.camunda.community.migration.processInstance.dto;

public class ProcessInstanceMigrationStartRequestDto {
  private String bpmnProcessId;

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }
}
