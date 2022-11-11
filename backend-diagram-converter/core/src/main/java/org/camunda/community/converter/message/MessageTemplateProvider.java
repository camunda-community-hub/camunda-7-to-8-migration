package org.camunda.community.converter.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MessageTemplateProvider {
  private static final MessageTemplateProcessor MESSAGE_TEMPLATE_PROCESSOR =
      new MessageTemplateProcessor();
  private static final Map<String, MessageTemplate> MESSAGE_TEMPLATES;

  static {
    MESSAGE_TEMPLATES = new HashMap<>();
    Properties loader = new Properties();
    try (InputStream in =
        Message.class.getClassLoader().getResourceAsStream("message-templates.properties")) {
      loader.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Map<String, String> rawTemplates = new HashMap<>();
    loader.stringPropertyNames().forEach(key -> rawTemplates.put(key, loader.getProperty(key)));
    MESSAGE_TEMPLATE_PROCESSOR.replaceTemplates(rawTemplates);
    rawTemplates
        .entrySet()
        .forEach(
            entry ->
                MESSAGE_TEMPLATES.put(
                    entry.getKey(),
                    new MessageTemplate(
                        entry.getValue(),
                        MESSAGE_TEMPLATE_PROCESSOR.extractVariables(entry.getValue()))));
  }

  public MessageTemplate getMessageTemplate(String templateName) {
    MessageTemplate template = null;
    synchronized (MESSAGE_TEMPLATES) {
      template = MESSAGE_TEMPLATES.get(templateName);
    }
    if (template == null) {
      throw new IllegalStateException("No template found for name '" + templateName + "'");
    }
    return template;
  }
}
