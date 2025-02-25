package org.camunda.community.migration.converter.convertible;

public class TextConvertible extends AbstractDmnConvertible {
  private String content;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
