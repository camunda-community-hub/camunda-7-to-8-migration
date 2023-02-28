package org.camunda.community.migration.processInstance.dto.rest;

import org.camunda.community.migration.processInstance.dto.task.UserTask.TaskState;

public class UserTaskDto {
  private Long key;
  private Long processInstanceKey;
  private String name;
  private String type;
  private UserTaskDataDto data;
  private TaskState state;

  public Long getProcessInstanceKey() {
    return processInstanceKey;
  }

  public void setProcessInstanceKey(Long processInstanceKey) {
    this.processInstanceKey = processInstanceKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UserTaskDataDto getData() {
    return data;
  }

  public void setData(UserTaskDataDto data) {
    this.data = data;
  }

  public TaskState getState() {
    return state;
  }

  public void setState(TaskState state) {
    this.state = state;
  }

  public abstract static class UserTaskDataDto {}
}
