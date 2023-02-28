package org.camunda.community.migration.processInstance.dto.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Map;

public class VariableInstanceDto {
  private String id;
  private String name;
  private String processDefinitionId;
  private String processInstanceId;
  private String executionId;
  private String taskId;
  private String batchId;
  private String activityInstanceId;
  private String tenantId;
  private String errorMessage;
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

  public static class VariableInstanceQueryResultDto extends ArrayList<VariableInstanceDto> {}
}
