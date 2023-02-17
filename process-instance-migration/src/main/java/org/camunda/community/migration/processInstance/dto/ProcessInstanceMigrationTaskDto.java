package org.camunda.community.migration.processInstance.dto;

import java.util.List;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceSelectionTask.TaskState;

public class ProcessInstanceMigrationTaskDto {
  private Long id;
  private String bpmnProcessId;
  private String processDefinitionId;
  private Long processDefinitionKey;
  private List<ProcessInstanceDataDto> availableProcessInstances;
  private TaskState state;

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public List<ProcessInstanceDataDto> getAvailableProcessInstances() {
    return availableProcessInstances;
  }

  public void setAvailableProcessInstances(List<ProcessInstanceDataDto> availableProcessInstances) {
    this.availableProcessInstances = availableProcessInstances;
  }

  public Long getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(Long processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
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
