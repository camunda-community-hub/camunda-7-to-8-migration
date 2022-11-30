package org.camunda.community.migration.converter.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageLinkProvider {
  private static final Properties MESSAGE_LINKS;

  static {
    MESSAGE_LINKS = new Properties();
    try (InputStream in =
        Message.class.getClassLoader().getResourceAsStream("message-links.properties")) {
      MESSAGE_LINKS.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getMessageLink(String templateName) {
    String link;
    synchronized (MESSAGE_LINKS) {
      link = MESSAGE_LINKS.getProperty(templateName);
    }
    return link;
  }
}
