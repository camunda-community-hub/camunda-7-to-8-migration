package org.camunda.community.migration.processInstance.dto.task;

import java.util.Map;
import org.camunda.community.migration.processInstance.dto.task.UserTask.UserTaskData;

public class RouteExecutionCancellationTaskData extends UserTaskData {
  private final String camunda7ProcessDefinitionId;
  private final Map<String, String> camunda7JobDefinitions;

  public RouteExecutionCancellationTaskData(
      String camunda7ProcessDefinitionId, Map<String, String> camunda7JobDefinitions) {
    this.camunda7ProcessDefinitionId = camunda7ProcessDefinitionId;
    this.camunda7JobDefinitions = camunda7JobDefinitions;
  }

  public String getCamunda7ProcessDefinitionId() {
    return camunda7ProcessDefinitionId;
  }

  public Map<String, String> getCamunda7JobDefinitions() {
    return camunda7JobDefinitions;
  }
}
