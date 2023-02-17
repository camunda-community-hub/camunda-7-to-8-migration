package org.camunda.community.migration.processInstance.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;

public class Camunda7ProcessInstanceData {
  private String processInstanceId;
  private String processDefinitionKey;
  private Map<String, ProcessVariableData> processVariables;
  private List<ActivityData> activities;

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public Map<String, ProcessVariableData> getProcessVariables() {
    return processVariables;
  }

  public void setProcessVariables(Map<String, ProcessVariableData> processVariables) {
    this.processVariables = processVariables;
  }

  public List<ActivityData> getActivities() {
    return activities;
  }

  public void setActivities(List<ActivityData> activities) {
    this.activities = activities;
  }

  public static class ProcessVariableData {
    private JsonNode value;
    private String scope;

    public static ProcessVariableData of(JsonNode value, String scope) {
      ProcessVariableData data = new ProcessVariableData();
      data.setValue(value);
      data.setScope(scope);
      return data;
    }

    public JsonNode getValue() {
      return value;
    }

    public void setValue(JsonNode value) {
      this.value = value;
    }

    public String getScope() {
      return scope;
    }

    public void setScope(String scope) {
      this.scope = scope;
    }

    @Override
    public String toString() {
      return "ProcessVariableData{" + "value=" + value + ", scope='" + scope + '\'' + '}';
    }
  }

  public static class ActivityData {
    private String id;
    private String type;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return "ActivityData{" + "id='" + id + '\'' + ", type='" + type + '\'' + '}';
    }
  }
}
