package org.camunda.community.migration.processInstance.core.dto;

import java.util.Map;

public class Camunda7ProcessData {
  private String processDefinitionKey;
  private Map<String, Object> processVariables;

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
}
