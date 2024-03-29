package org.camunda.community.migration.converter.message;

import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;

public class ComposedMessage implements Message {
  private Severity severity;
  private String message;
  private String link;

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }
}
