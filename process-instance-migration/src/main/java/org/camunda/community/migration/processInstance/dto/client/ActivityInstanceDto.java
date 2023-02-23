package org.camunda.community.migration.processInstance.dto.client;

import java.util.List;

public class ActivityInstanceDto {
  private String activityId;
  private String parentActivityInstanceId;
  private List<ActivityInstanceDto> childActivityInstances;
  private String id;
  private String activityName;
  private String activityType;
  private String processInstanceId;
  private String processDefinitionId;
  private List<TransitionInstanceDto> childTransitionInstances;
  private List<String> executionIds;
  private List<String> incidentIds;
  private List<ActivityInstanceIncidentDto> incidents;

  public List<TransitionInstanceDto> getChildTransitionInstances() {
    return childTransitionInstances;
  }

  public void setChildTransitionInstances(List<TransitionInstanceDto> childTransitionInstances) {
    this.childTransitionInstances = childTransitionInstances;
  }

  public List<String> getExecutionIds() {
    return executionIds;
  }

  public void setExecutionIds(List<String> executionIds) {
    this.executionIds = executionIds;
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

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getActivityType() {
    return activityType;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

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

  public List<ActivityInstanceDto> getChildActivityInstances() {
    return childActivityInstances;
  }

  public void setChildActivityInstances(List<ActivityInstanceDto> childActivityInstances) {
    this.childActivityInstances = childActivityInstances;
  }
}
