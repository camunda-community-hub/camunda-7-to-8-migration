package org.camunda.community.migration.converter.message;

import java.util.List;
import org.camunda.community.migration.converter.DiagramCheckResult.Severity;

public class MessageTemplate {
  private final Severity severity;
  private final String link;
  private final String template;
  private final List<String> variables;

  public MessageTemplate(Severity severity, String link, String template, List<String> variables) {
    this.severity = severity;
    this.link = link;
    this.template = template;
    this.variables = variables;
  }

  public String getTemplate() {
    return template;
  }

  public List<String> getVariables() {
    return variables;
  }

  public Severity getSeverity() {
    return severity;
  }

  public String getLink() {
    return link;
  }
}
