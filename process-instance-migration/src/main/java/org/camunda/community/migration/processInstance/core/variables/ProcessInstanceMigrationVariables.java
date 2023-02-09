package org.camunda.community.migration.processInstance.core.variables;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class ProcessInstanceMigrationVariables {
  private String bpmnProcessId;
  private String camunda7ProcessDefinitionId;
  private String camunda7ProcessInstanceId;
  private Long camunda8ProcessInstanceKey;
  private List<String> activityIds;
  private Map<String, Object> variables;
  private Boolean alreadyConverted;

  public String getCamunda7ProcessDefinitionId() {
    return camunda7ProcessDefinitionId;
  }

  public void setCamunda7ProcessDefinitionId(String camunda7ProcessDefinitionId) {
    this.camunda7ProcessDefinitionId = camunda7ProcessDefinitionId;
  }

  public Boolean getAlreadyConverted() {
    return alreadyConverted;
  }

  public void setAlreadyConverted(Boolean alreadyConverted) {
    this.alreadyConverted = alreadyConverted;
  }

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

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }
}
