package org.camunda.community.migration.processInstance.exporter.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface VariableInstance {
  String getName();

  JsonNode getValue();
}
