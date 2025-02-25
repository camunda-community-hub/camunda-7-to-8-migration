package org.camunda.community.migration.converter.message;

import org.camunda.community.migration.converter.DiagramCheckResult.Severity;

public class ComposedMessage implements Message {
  private Severity severity;
  private String message;
  private String link;
  private String id;

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

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
