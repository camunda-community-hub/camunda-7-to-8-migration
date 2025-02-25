package org.camunda.community.migration.converter.convertible;

public class InputExpressionConvertible extends AbstractTypeRefConvertible
    implements TextConvertible {
  private String content;

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }
}
