package org.camunda.community.migration.processInstance.core;

public class ProcessInstanceSelectionTask {
  private final Long jobKey;
  private final Long processInstanceKey;
  private final String bpmnProcessId;
  private final String processDefinitionId;
  private final TaskState state;

  public ProcessInstanceSelectionTask(
      Long jobKey,
      Long processInstanceKey,
      String bpmnProcessId,
      String processDefinitionId,
      TaskState state) {
    this.jobKey = jobKey;
    this.processInstanceKey = processInstanceKey;
    this.bpmnProcessId = bpmnProcessId;
    this.processDefinitionId = processDefinitionId;
    this.state = state;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public Long getProcessInstanceKey() {
    return processInstanceKey;
  }

  public Long getJobKey() {
    return jobKey;
  }

  public TaskState getState() {
    return state;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public ProcessInstanceSelectionTask state(TaskState state) {
    return new ProcessInstanceSelectionTask(
        jobKey, processInstanceKey, bpmnProcessId, processDefinitionId, state);
  }

  public enum TaskState {
    CREATED,
    COMPLETED
  }
}
