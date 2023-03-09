package org.camunda.community.migration.processInstance.api.model.data.chunk;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public interface VariableScopeData {
  Map<String, JsonNode> getVariables();

  interface VariableScopeDataBuilder<T> {
    T withVariable(String variableName, JsonNode variableValue);

    T withVariables(Map<String, JsonNode> variables);
  }
}
