package org.camunda.community.migration.processInstance.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.task.UserTask;
import org.camunda.community.migration.processInstance.dto.task.UserTask.TaskState;
import org.camunda.community.migration.processInstance.variables.ProcessInstanceMigrationVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrationTaskService {
  private static final Logger LOG = LoggerFactory.getLogger(MigrationTaskService.class);
  private final Camunda8Service camunda8Service;
  private final Map<Long, UserTask> taskList = new HashMap<>();
  private final Map<Long, LocalDateTime> timeouts = new HashMap<>();

  @Autowired
  public MigrationTaskService(Camunda8Service camunda8Service) {
    this.camunda8Service = camunda8Service;
  }

  public List<UserTask> getTasks(boolean includeCompleted) {
    Set<Long> removed = new HashSet<>();
    timeouts.forEach(
        (key, timeout) -> {
          if (timeout.isBefore(LocalDateTime.now())) {
            LOG.info("Task with key '{}' timed out", key);
            taskList.remove(key);
            removed.add(key);
          }
        });
    removed.forEach(timeouts::remove);
    return taskList.values().stream()
        .filter(task -> includeCompleted || task.getState().equals(TaskState.CREATED))
        .collect(Collectors.toList());
  }

  public UserTask getTask(long jobKey) {
    return taskList.get(jobKey);
  }

  public void addTask(UserTask task) {
    timeouts.put(task.getKey(), LocalDateTime.now().plusMinutes(1));
    if (task.getState().equals(TaskState.CREATED)) {
      UserTask fromList = taskList.get(task.getKey());
      if (fromList != null && fromList.getState().equals(TaskState.COMPLETED)) {
        return;
      }
    }
    LOG.info("Added task with key '{}'", task.getKey());
    taskList.put(task.getKey(), task);
  }

  public UserTask complete(long jobKey, ProcessInstanceMigrationVariables result) {
    UserTask fromList = taskList.get(jobKey);
    if (fromList != null) {
      camunda8Service.completeTask(jobKey, result);
      fromList = fromList.state(TaskState.COMPLETED);
      taskList.put(jobKey, fromList);
      return fromList;
    }
    throw new IllegalStateException("Task with key '" + jobKey + "' does not exist");
  }
}
