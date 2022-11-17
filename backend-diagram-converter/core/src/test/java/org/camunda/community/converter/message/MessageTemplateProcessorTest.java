package org.camunda.community.converter.message;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MessageTemplateProcessorTest {
  private static final MessageTemplateProcessor MESSAGE_TEMPLATE_PROCESSOR =
      new MessageTemplateProcessor();

  @Test
  void shouldDecorateTemplateString() {
    MessageTemplate template =
        new MessageTemplate("Easy {{ template }}", Collections.singletonList("template"));
    Map<String, String> context = ContextBuilder.builder().entry("template", "${pisi}").build();
    String message = MESSAGE_TEMPLATE_PROCESSOR.decorate(template, context);
    assertEquals("Easy ${pisi}", message);
  }

  @Test
  void shouldThrowIfVariableNotPresent() {
    MessageTemplate template =
        new MessageTemplate("Easy {{ template }}", Collections.singletonList("template"));
    Map<String, String> context = ContextBuilder.builder().entry("template2", "pisi").build();
    assertThrows(
        IllegalStateException.class, () -> MESSAGE_TEMPLATE_PROCESSOR.decorate(template, context));
  }

  @Test
  void shouldCreateOnlyUniqueVariables() {
    List<String> variables =
        MESSAGE_TEMPLATE_PROCESSOR.extractVariables("Hello {{ world }}, this is {{ world }}");
    assertThat(variables).hasSize(1).containsExactly("world");
  }

  @Test
  void shouldReplaceAllVariablesWithSameName() {
    MessageTemplate messageTemplate =
        new MessageTemplate(
            "Hello {{ world }}, this is {{ world }}", Collections.singletonList("world"));
    Map<String, String> context = ContextBuilder.builder().entry("world", "Tim").build();
    String message = MESSAGE_TEMPLATE_PROCESSOR.decorate(messageTemplate, context);
    assertEquals("Hello Tim, this is Tim", message);
  }
}
