package org.camunda.community.migration.processInstance.dto;

import java.util.List;

public class TransitionInstanceDto {
  private String id;
  private String parentActivityInstanceId;
  private String activityId;
  private String activityName;
  private String activityType;
  private String processInstanceId;
  private String processDefinitionId;
  private String executionId;
  private List<String> incidentIds;
  private List<ActivityInstanceIncidentDto> incidents;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getParentActivityInstanceId() {
    return parentActivityInstanceId;
  }

  public void setParentActivityInstanceId(String parentActivityInstanceId) {
    this.parentActivityInstanceId = parentActivityInstanceId;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public List<String> getIncidentIds() {
    return incidentIds;
  }

  public void setIncidentIds(List<String> incidentIds) {
    this.incidentIds = incidentIds;
  }

  public List<ActivityInstanceIncidentDto> getIncidents() {
    return incidents;
  }

  public void setIncidents(List<ActivityInstanceIncidentDto> incidents) {
    this.incidents = incidents;
  }
}
