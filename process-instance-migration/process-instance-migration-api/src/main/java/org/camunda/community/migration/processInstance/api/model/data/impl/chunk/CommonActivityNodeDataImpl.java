package org.camunda.community.migration.processInstance.api.model.data.impl.chunk;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Objects;
import org.camunda.community.migration.processInstance.api.model.data.chunk.CommonActivityNodeData;

public abstract class CommonActivityNodeDataImpl implements CommonActivityNodeData {
  private String name;
  private Map<String, JsonNode> variables;
  private Boolean executed;

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

  @Override
  public Boolean getExecuted() {
    return executed;
  }

  public void setExecuted(Boolean executed) {
    this.executed = executed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommonActivityNodeDataImpl that = (CommonActivityNodeDataImpl) o;
    return Objects.equals(name, that.name)
        && Objects.equals(variables, that.variables)
        && Objects.equals(executed, that.executed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, variables, executed);
  }
}
