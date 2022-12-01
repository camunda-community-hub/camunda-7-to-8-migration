package org.camunda.community.migration.converter.message;

import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;

public interface Message {
  Severity getSeverity();

  String getMessage();

  String getLink();
}
