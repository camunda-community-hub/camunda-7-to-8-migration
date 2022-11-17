package org.camunda.community.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ConverterPropertiesTest {
  @Test
  void shouldContainValues() {
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    assertNotNull(properties.getScriptHeader());
  }
}
