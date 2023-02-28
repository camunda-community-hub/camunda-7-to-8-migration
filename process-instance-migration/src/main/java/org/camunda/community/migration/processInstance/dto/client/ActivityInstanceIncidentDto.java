package org.camunda.community.migration.processInstance.dto.client;

public class ActivityInstanceIncidentDto {
  private String id;
  private String activityId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }
}
