package org.camunda.community.converter.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EmptyMessageTest {

  @Test
  void shouldNotReturnNull() {
    Message message = new EmptyMessage();
    assertNotNull(message.getMessage());
    assertNotNull(message.getLink());
  }
}
