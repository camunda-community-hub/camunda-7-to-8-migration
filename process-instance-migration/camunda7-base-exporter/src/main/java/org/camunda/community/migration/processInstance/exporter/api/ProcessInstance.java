package org.camunda.community.migration.processInstance.exporter.api;

public interface ProcessInstance {
  String getId();

  String getProcessDefinitionId();

  String getSuperExecutionId();
}
