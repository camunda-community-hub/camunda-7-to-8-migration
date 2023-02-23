package org.camunda.community.migration.processInstance.dto.client;

import java.util.ArrayList;

public class JobDefinitionDto {
  private String id;
  private String processDefinitionId;
  private String processDefinitionKey;
  private String jobType;
  private String jobConfiguration;
  private String activityId;
  private Boolean suspended;
  private Integer overridingJobPriority;
  private String tenantId;
  private String deploymentId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public static class JobDefinitionQueryResultDto extends ArrayList<JobDefinitionDto> {}
}
