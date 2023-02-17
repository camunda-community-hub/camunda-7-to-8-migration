package org.camunda.community.migration.processInstance.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceSelectionTask;
import org.camunda.community.migration.processInstance.dto.ProcessInstanceSelectionTask.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceSelectionService {
  private static final Logger LOG = LoggerFactory.getLogger(ProcessInstanceSelectionService.class);
  private final Camunda8Service camunda8Service;
  private final Map<Long, ProcessInstanceSelectionTask> taskList = new HashMap<>();
  private final Map<Long, LocalDateTime> timeouts = new HashMap<>();

  @Autowired
  public ProcessInstanceSelectionService(Camunda8Service camunda8Service) {
    this.camunda8Service = camunda8Service;
  }

  public List<ProcessInstanceSelectionTask> getTasks(boolean includeCompleted) {
    Set<Long> removed = new HashSet<>();
    timeouts.forEach(
        (key, timeout) -> {
          if (timeout.isBefore(LocalDateTime.now())) {
            LOG.info("Task {} timed out", key);
            taskList.remove(key);
            removed.add(key);
          }
        });
    removed.forEach(timeouts::remove);
    return taskList.values().stream()
        .filter(task -> includeCompleted || task.getState().equals(TaskState.CREATED))
        .collect(Collectors.toList());
  }

  public ProcessInstanceSelectionTask getTask(long jobKey) {
    return taskList.get(jobKey);
  }

  public void addTask(ProcessInstanceSelectionTask task) {
    timeouts.put(task.getJobKey(), LocalDateTime.now().plusMinutes(5));
    if (task.getState().equals(TaskState.CREATED)) {
      ProcessInstanceSelectionTask fromList = taskList.get(task.getJobKey());
      if (fromList != null && fromList.getState().equals(TaskState.COMPLETED)) {
        return;
      }
    }
    taskList.put(task.getJobKey(), task);
  }

  public ProcessInstanceSelectionTask complete(long jobKey, List<String> processInstances) {
    ProcessInstanceSelectionTask fromList = taskList.get(jobKey);
    if (fromList != null) {
      camunda8Service.selectProcessInstances(jobKey, processInstances);
      fromList = fromList.state(TaskState.COMPLETED);
      taskList.put(jobKey, fromList);
      return fromList;
    }
    throw new IllegalStateException("Task with id '" + jobKey + "' does not exist");
  }
}
