package org.camunda.community.migration.converter.message;

public class EmptyMessage implements Message {

  @Override
  public String getMessage() {
    return "";
  }

  @Override
  public String getLink() {
    return "";
  }
}
