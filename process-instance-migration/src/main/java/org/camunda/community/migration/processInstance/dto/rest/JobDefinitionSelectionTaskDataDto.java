package org.camunda.community.migration.processInstance.dto.rest;

import java.util.Map;
import org.camunda.community.migration.processInstance.dto.rest.UserTaskDto.UserTaskDataDto;

public class JobDefinitionSelectionTaskDataDto extends UserTaskDataDto {
  private String camunda7ProcessDefinitionId;
  private Map<String, String> camunda7JobDefinitions;

  public String getCamunda7ProcessDefinitionId() {
    return camunda7ProcessDefinitionId;
  }

  public void setCamunda7ProcessDefinitionId(String camunda7ProcessDefinitionId) {
    this.camunda7ProcessDefinitionId = camunda7ProcessDefinitionId;
  }

  public Map<String, String> getCamunda7JobDefinitions() {
    return camunda7JobDefinitions;
  }

  public void setCamunda7JobDefinitions(Map<String, String> camunda7JobDefinitions) {
    this.camunda7JobDefinitions = camunda7JobDefinitions;
  }
}
