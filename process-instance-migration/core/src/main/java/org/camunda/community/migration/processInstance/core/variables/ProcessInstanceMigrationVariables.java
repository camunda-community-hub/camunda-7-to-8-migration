package org.camunda.community.migration.processInstance.core.variables;

import java.util.List;
import java.util.Map;

public class ProcessInstanceMigrationVariables {
  private String camunda7ProcessInstanceId;
  private Long camunda8ProcessInstanceKey;
  private List<String> activityIds;
  private Map<String,Object> variables;

  public String getCamunda7ProcessInstanceId() {
    return camunda7ProcessInstanceId;
  }

  public void setCamunda7ProcessInstanceId(String camunda7ProcessInstanceId) {
    this.camunda7ProcessInstanceId = camunda7ProcessInstanceId;
  }

  public Long getCamunda8ProcessInstanceKey() {
    return camunda8ProcessInstanceKey;
  }

  public void setCamunda8ProcessInstanceKey(Long camunda8ProcessInstanceKey) {
    this.camunda8ProcessInstanceKey = camunda8ProcessInstanceKey;
  }

  public List<String> getActivityIds() {
    return activityIds;
  }

  public void setActivityIds(List<String> activityIds) {
    this.activityIds = activityIds;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
  }
}
