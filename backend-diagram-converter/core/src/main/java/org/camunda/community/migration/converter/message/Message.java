package org.camunda.community.migration.converter.message;

import org.camunda.community.migration.converter.DiagramCheckResult.Severity;

public interface Message {
  Severity getSeverity();

  String getMessage();

  String getLink();

  String getId();
}
