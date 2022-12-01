package org.camunda.community.migration.converter.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.Severity;

public class MessageTemplateProvider {
  private static final String PROPERTY_MESSAGE = "message";
  private static final String PROPERTY_LINK = "link";
  private static final String PROPERTY_SEVERITY = "severity";
  private static final MessageTemplateProcessor MESSAGE_TEMPLATE_PROCESSOR =
      new MessageTemplateProcessor();
  private static final Map<String, MessageTemplate> MESSAGE_TEMPLATES;
  private static final List<String> BOOTSTRAP_ERRORS = new ArrayList<>();

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
    Map<String, Map<String, String>> messagesTemplates = new HashMap<>();
    for (String name : loader.stringPropertyNames()) {
      if (isBuildingBlock(name)) {
        String templateName = name.split("\\.")[1];
        rawTemplates.put(templateName, loader.getProperty(name));
      } else {
        if (!assertName(name)) {
          continue;
        }
        String[] split = name.split("\\.");
        String messageName = split[0];
        String property = split[1];
        if (property.equals(PROPERTY_MESSAGE)) {
          rawTemplates.put(messageName, loader.getProperty(name));
        }
        if (property.equals(PROPERTY_SEVERITY)) {
          if (!assertSeverity(messageName, loader.getProperty(name))) {
            continue;
          }
        }
        Map<String, String> messageProperties =
            messagesTemplates.computeIfAbsent(messageName, (s) -> new HashMap<>());
        assertMessageProperties(messageName, messageProperties, property);
        messageProperties.put(property, loader.getProperty(name));
      }
    }
    if (assertMandatoryEntries(messagesTemplates)) {
      MESSAGE_TEMPLATE_PROCESSOR.replaceTemplates(rawTemplates);
      rawTemplates.forEach(
          (messageName, template) -> {
            if (messagesTemplates.containsKey(messageName)) {
              messagesTemplates.get(messageName).put(PROPERTY_MESSAGE, template);
            }
          });
      messagesTemplates.forEach(
          (key, value) ->
              MESSAGE_TEMPLATES.put(
                  key,
                  new MessageTemplate(
                      Severity.valueOf(value.get(PROPERTY_SEVERITY)),
                      value.get(PROPERTY_LINK),
                      value.get(PROPERTY_MESSAGE),
                      MESSAGE_TEMPLATE_PROCESSOR.extractVariables(value.get(PROPERTY_MESSAGE)))));
    }
    if (!BOOTSTRAP_ERRORS.isEmpty()) {
      throw new IllegalArgumentException(
          BOOTSTRAP_ERRORS.size()
              + " Errors while parsing message templates: \n- "
              + String.join("\n- ", BOOTSTRAP_ERRORS));
    }
  }

  private static boolean isBuildingBlock(String name) {
    return "template".equals(name.split("\\.")[0]);
  }

  private static boolean assertMandatoryEntries(
      Map<String, Map<String, String>> messagesTemplates) {
    boolean allFine = true;
    for (Entry<String, Map<String, String>> e : messagesTemplates.entrySet()) {
      for (String property : Arrays.asList(PROPERTY_SEVERITY, PROPERTY_MESSAGE)) {
        if (!e.getValue().containsKey(property)) {
          BOOTSTRAP_ERRORS.add("Message '" + e.getKey() + "' needs a property '" + property + "'");
          allFine = false;
        }
      }
    }
    return allFine;
  }

  private static void assertMessageProperties(
      String messageName, Map<String, String> messageProperties, String property) {
    if (messageProperties.containsKey(property)) {
      BOOTSTRAP_ERRORS.add(
          "Defined multiple properties " + property + " for message " + messageName);
    }
  }

  private static boolean assertName(String propertyName) {
    String[] split = propertyName.split("\\.");
    if (split.length != 2
        || !Arrays.asList(PROPERTY_LINK, PROPERTY_MESSAGE, PROPERTY_SEVERITY).contains(split[1])) {
      BOOTSTRAP_ERRORS.add(
          "Error while parsing message templates: Property '"
              + propertyName
              + "' needs to consist of <MESSAGE_NAME>.<"
              + String.join("|", PROPERTY_LINK, PROPERTY_MESSAGE, PROPERTY_SEVERITY)
              + ">");
      return false;
    }
    return true;
  }

  private static boolean assertSeverity(String messageName, String severity) {
    try {
      Severity.valueOf(severity);
    } catch (IllegalArgumentException e) {
      BOOTSTRAP_ERRORS.add(
          "Severity '"
              + severity
              + "' for message '"
              + messageName
              + "' is not defined. Please use one of '"
              + Arrays.stream(Severity.values())
                  .map(Severity::name)
                  .collect(Collectors.joining("', '"))
              + "'");
      return false;
    }
    return true;
  }

  public MessageTemplate getMessageTemplate(String templateName) {
    MessageTemplate template;
    synchronized (MESSAGE_TEMPLATES) {
      template = MESSAGE_TEMPLATES.get(templateName);
    }
    if (template == null) {
      throw new IllegalStateException("No template found for name '" + templateName + "'");
    }
    return template;
  }
}
