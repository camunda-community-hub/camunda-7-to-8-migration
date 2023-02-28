package org.camunda.community.migration.processInstance.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintRule.ProcessInstanceMigrationHintRuleContext;

public class Camunda7ProcessInstanceData implements ProcessInstanceMigrationHintRuleContext {
  private String processInstanceId;
  private String processDefinitionKey;
  private String businessKey;
  private Map<String, ProcessVariableData> processVariables;
  private List<ActivityData> activities;
  private List<String> migrationHints;
  private List<JobData> jobData;

  public List<JobData> getJobData() {
    return jobData;
  }

  public void setJobData(List<JobData> jobData) {
    this.jobData = jobData;
  }

  public String getBusinessKey() {
    return businessKey;
  }

  public void setBusinessKey(String businessKey) {
    this.businessKey = businessKey;
  }

  public List<String> getMigrationHints() {
    return migrationHints;
  }

  public void setMigrationHints(List<String> migrationHints) {
    this.migrationHints = migrationHints;
  }

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

  @Override
  public String toString() {
    return "Camunda7ProcessInstanceData{"
        + "processInstanceId='"
        + processInstanceId
        + '\''
        + ", processDefinitionKey='"
        + processDefinitionKey
        + '\''
        + ", businessKey='"
        + businessKey
        + '\''
        + ", processVariables="
        + processVariables
        + ", activities="
        + activities
        + ", migrationHints="
        + migrationHints
        + '}';
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
    private Boolean leaf;

    public Boolean getLeaf() {
      return leaf;
    }

    public void setLeaf(Boolean leaf) {
      this.leaf = leaf;
    }

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

  public static class JobData {
    private String id;
    private String jobDefinitionId;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getJobDefinitionId() {
      return jobDefinitionId;
    }

    public void setJobDefinitionId(String jobDefinitionId) {
      this.jobDefinitionId = jobDefinitionId;
    }

    @Override
    public String toString() {
      return "JobData{" + "id='" + id + '\'' + ", jobDefinitionId='" + jobDefinitionId + '\'' + '}';
    }
  }
}
