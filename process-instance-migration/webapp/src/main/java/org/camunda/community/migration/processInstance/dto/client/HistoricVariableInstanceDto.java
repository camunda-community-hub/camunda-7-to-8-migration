package org.camunda.community.migration.processInstance.dto.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

public class HistoricVariableInstanceDto {
  private String id;
  private String name;
  private String processDefinitionKey;
  private String processDefinitionId;
  private String processInstanceId;
  private String executionId;
  private String activityInstanceId;
  private String taskId;
  private String tenantId;
  private String errorMessage;
  private String state;
  private ZonedDateTime createTime;
  private ZonedDateTime removalTime;
  private String rootProcessInstanceId;
  private JsonNode value;
  private String type;
  private Map<String, JsonNode> valueInfo;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
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

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
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

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public ZonedDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(ZonedDateTime createTime) {
    this.createTime = createTime;
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

  public JsonNode getValue() {
    return value;
  }

  public void setValue(JsonNode value) {
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, JsonNode> getValueInfo() {
    return valueInfo;
  }

  public void setValueInfo(Map<String, JsonNode> valueInfo) {
    this.valueInfo = valueInfo;
  }

  public static class HistoricVariableInstanceQueryResultDto
      extends ArrayList<HistoricVariableInstanceDto> {}
}
