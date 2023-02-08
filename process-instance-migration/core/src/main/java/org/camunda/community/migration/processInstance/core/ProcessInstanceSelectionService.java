package org.camunda.community.migration.processInstance.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.core.ProcessInstanceSelectionTask.TaskState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceSelectionService {
  private final ProcessInstanceMigrationService migrationService;
  private final Map<Long, ProcessInstanceSelectionTask> taskList = new HashMap<>();

  @Autowired
  public ProcessInstanceSelectionService(ProcessInstanceMigrationService migrationService) {
    this.migrationService = migrationService;
  }

  public List<ProcessInstanceSelectionTask> getTasks(boolean includeCompleted) {
    return taskList.values().stream()
        .filter(task -> includeCompleted || task.getState().equals(TaskState.CREATED))
        .collect(Collectors.toList());
  }

  public ProcessInstanceSelectionTask getTask(long jobKey) {
    return taskList.get(jobKey);
  }

  public void addTask(ProcessInstanceSelectionTask task) {
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
      migrationService.selectProcessInstances(jobKey, processInstances);
      fromList = fromList.state(TaskState.COMPLETED);
      taskList.put(jobKey, fromList);
      return fromList;
    }
    throw new IllegalStateException("Task with id '" + jobKey + "' does not exist");
  }
}
