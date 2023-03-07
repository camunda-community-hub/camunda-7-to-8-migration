package org.camunda.community.migration.processInstance.dto.task;

public class UserTask {
  private final Long key;
  private final String name;
  private final Long processInstanceKey;
  private final String type;
  private final UserTaskData data;
  private final TaskState state;

  public UserTask(
      Long key,
      String name,
      Long processInstanceKey,
      String type,
      UserTaskData data,
      TaskState state) {
    this.key = key;
    this.name = name;
    this.processInstanceKey = processInstanceKey;
    this.type = type;
    this.data = data;
    this.state = state;
  }

  public Long getKey() {
    return key;
  }

  public String getType() {
    return type;
  }

  public UserTaskData getData() {
    return data;
  }

  public TaskState getState() {
    return state;
  }

  public String getName() {
    return name;
  }

  public UserTask state(TaskState taskState) {
    return new UserTask(key, name, processInstanceKey, type, data, taskState);
  }

  public Long getProcessInstanceKey() {
    return processInstanceKey;
  }

  public enum TaskState {
    CREATED,
    COMPLETED,
    CANCELED
  }

  public abstract static class UserTaskData {
    public <T extends UserTaskData> T as(Class<T> type) {
      return type.cast(this);
    }
  }
}
