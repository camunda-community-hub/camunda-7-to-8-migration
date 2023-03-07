package org.camunda.community.migration.processInstance.api.model.impl.chunk;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.camunda.community.migration.processInstance.api.model.chunk.ActivityNodeData;

public abstract class ActivityNodeDataImpl implements ActivityNodeData {
  private String name;
  private Map<String, JsonNode> variables;

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Map<String, JsonNode> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, JsonNode> variables) {
    this.variables = variables;
  }
}
