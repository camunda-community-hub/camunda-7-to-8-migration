package org.camunda.community.migration.processInstance.core.dto;

import java.util.List;
import java.util.Map;

public class Camunda7ProcessData {
  private String processDefinitionKey;
  private Map<String, Object> processVariables;
  private List<String> activityIds;

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public Map<String, Object> getProcessVariables() {
    return processVariables;
  }

  public void setProcessVariables(Map<String, Object> processVariables) {
    this.processVariables = processVariables;
  }

  public List<String> getActivityIds() {
    return activityIds;
  }

  public void setActivityIds(List<String> activityIds) {
    this.activityIds = activityIds;
  }
}
