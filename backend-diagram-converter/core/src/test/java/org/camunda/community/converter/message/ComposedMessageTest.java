package org.camunda.community.converter.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ComposedMessageTest {
  @Test
  void shouldCreateMessage() {
    Message message =
        new ComposedMessage(
            "candidate-groups",
            ContextBuilder.builder()
                .entry("attributeLocalName", "candidateGroups")
                .entry("elementLocalName", "userTask")
                .entry("oldExpression", "management")
                .entry("newExpression", "management")
                .build());
    assertNotNull(message.getMessage());
    assertNotNull(message.getLink());
  }
}
