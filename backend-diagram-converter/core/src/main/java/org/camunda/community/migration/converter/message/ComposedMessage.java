package org.camunda.community.migration.converter.message;

public class ComposedMessage implements Message {

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
}
