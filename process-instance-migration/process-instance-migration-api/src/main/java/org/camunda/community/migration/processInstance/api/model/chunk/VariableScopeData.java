package org.camunda.community.migration.processInstance.api.model.chunk;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public interface VariableScopeData {
  Map<String, JsonNode> getVariables();

  interface VariableScopeDataBuilder<T> {
    T variable(String variableName, JsonNode variableValue);
  }
}
