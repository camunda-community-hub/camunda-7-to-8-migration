package org.camunda.community.migration.processInstance.dto.task;

import java.util.Map;
import org.camunda.community.migration.processInstance.dto.task.UserTask.UserTaskData;

public class JobDefinitionSelectionTaskData extends UserTaskData {
  private final String camunda7ProcessDefinitionId;

  private final Map<String, String> camunda7JobDefinitions;

  public JobDefinitionSelectionTaskData(
      String camunda7ProcessDefinitionId, Map<String, String> camunda7JobDefinitions) {
    this.camunda7ProcessDefinitionId = camunda7ProcessDefinitionId;
    this.camunda7JobDefinitions = camunda7JobDefinitions;
  }

  public Map<String, String> getCamunda7JobDefinitions() {
    return camunda7JobDefinitions;
  }

  public String getCamunda7ProcessDefinitionId() {
    return camunda7ProcessDefinitionId;
  }
}
