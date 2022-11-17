package org.camunda.community.converter;

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
    DefaultConverterProperties.setZeebeJobType("adapter", properties::setAdapterJobType);
    assertNull(properties.getBpmnNamespace());
    ConverterProperties converterProperties =
        ConverterPropertiesFactory.getInstance().merge(properties);
    assertEquals("adapter", converterProperties.getAdapterJobType().getType());
    assertNotNull(properties.getBpmnNamespace());
  }
}
