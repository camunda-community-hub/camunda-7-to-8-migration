package org.camunda.community.migration.converter.convertible;

public interface TextConvertible extends Convertible {
  String getContent();

  void setContent(String content);
}
