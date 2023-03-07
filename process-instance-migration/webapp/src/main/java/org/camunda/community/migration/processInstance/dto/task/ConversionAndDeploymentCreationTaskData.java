package org.camunda.community.migration.processInstance.dto.task;

import org.camunda.community.migration.processInstance.dto.task.UserTask.UserTaskData;

public class ConversionAndDeploymentCreationTaskData extends UserTaskData {
  private final String bpmnProcessId;

  public ConversionAndDeploymentCreationTaskData(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }
}
