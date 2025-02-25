package org.camunda.community.migration.converter.convertible;

public abstract class AbstractRuleConvertible extends AbstractDmnConvertible {
  private String text;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
