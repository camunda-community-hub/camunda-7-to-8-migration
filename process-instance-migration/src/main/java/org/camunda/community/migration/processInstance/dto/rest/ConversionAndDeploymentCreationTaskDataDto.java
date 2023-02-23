package org.camunda.community.migration.processInstance.dto.rest;

import org.camunda.community.migration.processInstance.dto.rest.UserTaskDto.UserTaskDataDto;

public class ConversionAndDeploymentCreationTaskDataDto extends UserTaskDataDto {
  private String bpmnProcessId;

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }
}
