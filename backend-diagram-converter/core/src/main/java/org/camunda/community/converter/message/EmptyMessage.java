package org.camunda.community.converter.message;

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
