package org.camunda.community.converter.message;

import java.util.List;

public class MessageTemplate {
  private final String template;
  private final List<String> variables;

  public MessageTemplate(String template, List<String> variables) {
    this.template = template;
    this.variables = variables;
  }

  public String getTemplate() {
    return template;
  }

  public List<String> getVariables() {
    return variables;
  }
}
