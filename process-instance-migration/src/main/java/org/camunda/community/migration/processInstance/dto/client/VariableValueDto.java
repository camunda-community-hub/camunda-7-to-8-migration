package org.camunda.community.migration.processInstance.dto.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public class VariableValueDto {
  private JsonNode value;
  private String type;
  private Map<String, JsonNode> valueInfo;

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
}
