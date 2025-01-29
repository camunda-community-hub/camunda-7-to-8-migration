package org.camunda.community.migration.converter.message;

import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;

public class EmptyMessage implements Message {

  @Override
  public Severity getSeverity() {
    return null;
  }

  @Override
  public String getMessage() {
    return "";
  }

  @Override
  public String getLink() {
    return "";
  }

  @Override
  public String getId() {
    return "";
  }
}
