package org.camunda.community.migration.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ConverterPropertiesTest {
  @Test
  void shouldContainValues() {
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    assertNotNull(properties.getScriptHeader());
  }

  @Test
  void shouldMergeProperties() {
    DefaultConverterProperties properties = new DefaultConverterProperties();
    properties.setDefaultJobType("adapter");
    assertNull(properties.getResourceHeader());
    ConverterProperties converterProperties =
        ConverterPropertiesFactory.getInstance().merge(properties);
    assertEquals("adapter", converterProperties.getDefaultJobType());
    assertNotNull(properties.getResourceHeader());
  }
}
