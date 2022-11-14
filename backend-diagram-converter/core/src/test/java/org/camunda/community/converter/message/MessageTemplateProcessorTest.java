package org.camunda.community.converter.message;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
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
}
