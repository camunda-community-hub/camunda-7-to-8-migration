package org.camunda.community.migration.processInstance.importer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InitialVariableContext(
    @JsonProperty(CAMUNDA_7_PROCESS_INSTANCE_ID_VARIABLE) String camunda7ProcessInstanceId) {
  public static final String CAMUNDA_7_PROCESS_INSTANCE_ID_VARIABLE = "camunda7ProcessInstanceId";
}
