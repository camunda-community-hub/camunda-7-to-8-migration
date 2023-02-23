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

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public String getJobType() {
    return jobType;
  }

  public void setJobType(String jobType) {
    this.jobType = jobType;
  }

  public String getJobConfiguration() {
    return jobConfiguration;
  }

  public void setJobConfiguration(String jobConfiguration) {
    this.jobConfiguration = jobConfiguration;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public Boolean getSuspended() {
    return suspended;
  }

  public void setSuspended(Boolean suspended) {
    this.suspended = suspended;
  }

  public Integer getOverridingJobPriority() {
    return overridingJobPriority;
  }

  public void setOverridingJobPriority(Integer overridingJobPriority) {
    this.overridingJobPriority = overridingJobPriority;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId) {
    this.deploymentId = deploymentId;
  }

  public static class JobDefinitionQueryResultDto extends ArrayList<JobDefinitionDto> {}
}
