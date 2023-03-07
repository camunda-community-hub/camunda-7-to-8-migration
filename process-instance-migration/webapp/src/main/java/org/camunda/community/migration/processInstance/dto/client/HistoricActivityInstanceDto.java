package org.camunda.community.migration.processInstance.dto.client;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class HistoricActivityInstanceDto {
  private String activityId;
  private String activityName;
  private String activityType;
  private String assignee;
  private String calledProcessInstanceId;
  private String calledCaseInstanceId;
  private Boolean canceled;
  private Boolean completeScope;
  private Long durationInMillis;
  private ZonedDateTime endTime;
  private String executionId;
  private String id;
  private String parentActivityInstanceId;
  private String processDefinitionId;
  private String processInstanceId;
  private ZonedDateTime startTime;
  private String taskId;
  private String tenantId;
  private ZonedDateTime removalTime;
  private String rootProcessInstanceId;

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

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getCalledProcessInstanceId() {
    return calledProcessInstanceId;
  }

  public void setCalledProcessInstanceId(String calledProcessInstanceId) {
    this.calledProcessInstanceId = calledProcessInstanceId;
  }

  public String getCalledCaseInstanceId() {
    return calledCaseInstanceId;
  }

  public void setCalledCaseInstanceId(String calledCaseInstanceId) {
    this.calledCaseInstanceId = calledCaseInstanceId;
  }

  public Boolean getCanceled() {
    return canceled;
  }

  public void setCanceled(Boolean canceled) {
    this.canceled = canceled;
  }

  public Boolean getCompleteScope() {
    return completeScope;
  }

  public void setCompleteScope(Boolean completeScope) {
    this.completeScope = completeScope;
  }

  public Long getDurationInMillis() {
    return durationInMillis;
  }

  public void setDurationInMillis(Long durationInMillis) {
    this.durationInMillis = durationInMillis;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
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

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public ZonedDateTime getRemovalTime() {
    return removalTime;
  }

  public void setRemovalTime(ZonedDateTime removalTime) {
    this.removalTime = removalTime;
  }

  public String getRootProcessInstanceId() {
    return rootProcessInstanceId;
  }

  public void setRootProcessInstanceId(String rootProcessInstanceId) {
    this.rootProcessInstanceId = rootProcessInstanceId;
  }

  public static class HistoricActivityInstanceQueryResultDto
      extends ArrayList<HistoricActivityInstanceDto> {}
}
