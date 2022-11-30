package org.camunda.community.migration.converter.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageTemplateProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(MessageTemplateProcessor.class);
  private static final String PREFIX = "\\{\\{ *";
  private static final String SUFFIX = " *}}";
  private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([^\\{\\{\\}\\}]*)}}");
  private static final String TEMPLATES_PREFIX = "templates.";

  public String decorate(MessageTemplate template, Map<String, String> context) {
    String templateString = template.getTemplate();
    List<String> missingVariables = new ArrayList<>();
    LOG.debug("Filling template '{}' with context {}", templateString, context);
    for (String variableName : template.getVariables()) {
      String value = context.get(variableName);
      if (value == null) {
        missingVariables.add(variableName);
      } else {
        value = value.replaceAll("\\$", "\\\\\\$");
        LOG.debug("Replacing {} with {}", variableName, value);
        templateString = templateString.replaceAll(PREFIX + variableName + SUFFIX, value);
        LOG.debug("Template after replacement: {}", templateString);
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
    LOG.debug("Extracting variables from '{}'", template);
    Matcher matcher = PLACEHOLDER.matcher(template);
    List<String> variables = new ArrayList<>();
    while (matcher.find()) {
      String group = matcher.group(1).trim();
      if (!group.startsWith("templates.") && !variables.contains(group)) {
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
        LOG.debug("Replacing templates in '{}'", template);
        Matcher matcher = PLACEHOLDER.matcher(template);
        while (matcher.find()) {
          String group = matcher.group(1).trim();
          if (group.startsWith(TEMPLATES_PREFIX)) {
            template =
                template.replaceAll(
                    PREFIX + group + SUFFIX,
                    templates.get(group.substring(TEMPLATES_PREFIX.length())));
          }
        }
        if (!template.equals(templateEntry.getValue())) {
          LOG.debug("Found templates to replace, template is now '{}'", template);
          alteredTemplates.put(templateEntry.getKey(), template);
          templateFound = true;
        }
      }
      templates.putAll(alteredTemplates);
    }
  }
}
