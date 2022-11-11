package org.camunda.community.converter.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTemplateProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(MessageTemplateProcessor.class);
  private static final String PREFIX = "\\{\\{ *";
  private static final String SUFFIX = " *}}";
  private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([^\\{\\{\\}\\}]*)}}");
  private static final String TEMPLATES_PREFIX = "templates.";

  public String decorate(MessageTemplate template, Map<String, String> context) {
    String templateString = template.getTemplate();
    List<String> missingVariables = new ArrayList<>();
    LOG.debug("Filling template {} with context {}", template, context);
    for (String variableName : template.getVariables()) {
      String value = context.get(variableName);
      if (value == null) {
        missingVariables.add(variableName);
      } else {
        LOG.debug("Replacing {} with {}", variableName, value);
        templateString = templateString.replaceAll(PREFIX + variableName + SUFFIX, value);
        LOG.debug("Template after replacement: {}", template);
      }
    }
    if (!missingVariables.isEmpty()) {
      throw new IllegalStateException(
          "Error while decorating template '"
              + template.getTemplate()
              + "': Variables '"
              + missingVariables
              + "' are missing");
    }
    return templateString;
  }

  public List<String> extractVariables(String template) {
    LOG.debug("Extracting variables from {}", template);
    Matcher matcher = PLACEHOLDER.matcher(template);
    List<String> variables = new ArrayList<>();
    while (matcher.find()) {
      String group = matcher.group().trim();
      if (!group.startsWith("templates.")) {
        LOG.debug("Found {}", group);
        variables.add(group);
      }
    }
    return variables;
  }

  public void replaceTemplates(Map<String, String> templates) {
    LOG.debug("Replacing templates");
    boolean templateFound = true;
    while (templateFound) {
      templateFound = false;
      Map<String, String> alteredTemplates = new HashMap<>();
      for (Entry<String, String> templateEntry : templates.entrySet()) {
        String template = templateEntry.getValue();
        Matcher matcher = PLACEHOLDER.matcher(template);
        while (matcher.find()) {
          String group = matcher.group().trim();
          if (group.startsWith(TEMPLATES_PREFIX)) {
            template =
                template.replaceAll(
                    PREFIX + group + SUFFIX,
                    templates.get(group.substring(TEMPLATES_PREFIX.length())));
          }
        }
        if (!template.equals(templateEntry.getValue())) {
          alteredTemplates.put(templateEntry.getKey(), template);
        }
      }
      templates.putAll(alteredTemplates);
    }
  }
}
