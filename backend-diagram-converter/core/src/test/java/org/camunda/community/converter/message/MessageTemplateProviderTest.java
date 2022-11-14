package org.camunda.community.converter.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MessageTemplateProviderTest {
  private static final MessageTemplateProvider MESSAGE_TEMPLATE_PROVIDER =
      new MessageTemplateProvider();

  @Test
  void shouldFindExistingMessageTemplate() {
    MessageTemplate messageTemplate =
        MESSAGE_TEMPLATE_PROVIDER.getMessageTemplate("connector-hint");
    assertNotNull(messageTemplate);
  }

  @Test
  void shouldThrowIfNoTemplateCanBeFound() {
    assertThrows(
        IllegalStateException.class,
        () -> MESSAGE_TEMPLATE_PROVIDER.getMessageTemplate("non-existent"));
  }
}
