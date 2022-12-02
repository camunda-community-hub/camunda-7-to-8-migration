package org.camunda.community.migration.processInstance.core.dto;

import java.util.List;

public class ActivityInstanceDto {
  private String activityId;
  private List<ActivityInstanceDto> childActivityInstances;

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
