package org.camunda.community.migration.processInstance.dto.rest;

import java.util.List;
import org.camunda.community.migration.processInstance.dto.rest.UserTaskDto.UserTaskDataDto;

public class ProcessInstanceSelectionTaskDataDto extends UserTaskDataDto {
  private String bpmnProcessId;
  private List<ProcessInstanceDataDto> availableProcessInstances;

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }

  public List<ProcessInstanceDataDto> getAvailableProcessInstances() {
    return availableProcessInstances;
  }

  public void setAvailableProcessInstances(List<ProcessInstanceDataDto> availableProcessInstances) {
    this.availableProcessInstances = availableProcessInstances;
  }

  public static class ProcessInstanceDataDto {
    private String id;
    private String businessKey;
    private List<String> migrationHints;

    public List<String> getMigrationHints() {
      return migrationHints;
    }

    public void setMigrationHints(List<String> migrationHints) {
      this.migrationHints = migrationHints;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getBusinessKey() {
      return businessKey;
    }

    public void setBusinessKey(String businessKey) {
      this.businessKey = businessKey;
    }
  }
}
