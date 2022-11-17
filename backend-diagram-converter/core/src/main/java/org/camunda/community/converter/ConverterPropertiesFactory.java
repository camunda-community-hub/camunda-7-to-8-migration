package org.camunda.community.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.camunda.community.converter.ConverterProperties.AttributeProperties;
import org.camunda.community.converter.ConverterProperties.NamespaceProperties;
import org.camunda.community.converter.ConverterProperties.ZeebeHeaderProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultAttributeProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultNamespaceProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultZeebeHeaderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConverterPropertiesFactory extends AbstractFactory<ConverterProperties> {
  public static final String DELIMITER = ".";
  private static final Logger LOG = LoggerFactory.getLogger(ConverterPropertiesFactory.class);
  private static final ConverterPropertiesFactory INSTANCE = new ConverterPropertiesFactory();
  private static final Properties PROPERTIES = new Properties();

  static {
    try (InputStream in =
        ConverterPropertiesFactory.class
            .getClassLoader()
            .getResourceAsStream("converter-properties.properties")) {
      PROPERTIES.load(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private final Map<String, NamespaceProperties> namespacePropertiesCache = new HashMap<>();

  public static ConverterPropertiesFactory getInstance() {
    return INSTANCE;
  }

  @Override
  protected ConverterProperties createInstance() {
    return merge(new DefaultConverterProperties());
  }

  public ConverterProperties merge(DefaultConverterProperties properties) {
    readDefaultValues(properties);
    return properties;
  }

  private void readDefaultValues(DefaultConverterProperties properties) {
    readZeebeHeader("script", properties::setScriptHeader);
    readZeebeHeader("result-variable", properties::setResultVariableHeader);
    readZeebeHeader("adapter-job-type", properties::setAdapterJobTypeHeader);
    readZeebeHeader("resource", properties::setResourceHeader);
    readZeebeHeader("script-format", properties::setScriptFormatHeader);
    readNamespaceProperties("zeebe", properties::setZeebeNamespace);
    readNamespaceProperties("conversion", properties::setConversionNamespace);
    readNamespaceProperties("modeler", properties::setModelerNamespace);
    readNamespaceProperties("camunda", properties::setCamundaNamespace);
    readNamespaceProperties("bpmn", properties::setBpmnNamespace);
    readNamespaceProperties("xsi", properties::setXsiNamespace);
    readAttributeProperties("execution-platform", properties::setExecutionPlatformAttribute);
    readAttributeProperties(
        "execution-platform-version", properties::setExecutionPlatformVersionAttribute);
  }

  private void readZeebeHeader(String header, Consumer<ZeebeHeaderProperties> setter) {
    String prefix = String.join(DELIMITER, "zeebe-header", header);
    DefaultZeebeHeaderProperties properties = new DefaultZeebeHeaderProperties();
    readDefaultValue(prefix, properties::getName, properties::setName);
    setter.accept(properties);
  }

  void readNamespaceProperties(String namespace, Consumer<NamespaceProperties> setter) {
    String prefix = String.join(DELIMITER, "namespace", namespace);
    NamespaceProperties namespaceProperties =
        namespacePropertiesCache.computeIfAbsent(
            namespace,
            ns -> {
              DefaultNamespaceProperties properties = new DefaultNamespaceProperties();
              readDefaultValue(
                  String.join(DELIMITER, prefix, "name"), properties::getName, properties::setName);
              readDefaultValue(
                  String.join(DELIMITER, prefix, "uri"), properties::getUri, properties::setUri);
              return properties;
            });
    setter.accept(namespaceProperties);
  }

  private void readAttributeProperties(String attribute, Consumer<AttributeProperties> setter) {
    String prefix = String.join(DELIMITER, "attribute", attribute);
    DefaultAttributeProperties properties = new DefaultAttributeProperties();
    readDefaultValue(
        String.join(DELIMITER, prefix, "name"), properties::getName, properties::setName);
    readDefaultValue(
        String.join(DELIMITER, prefix, "value"), properties::getValue, properties::setValue);
    readDefaultValue(
        String.join(DELIMITER, prefix, "namespace"),
        properties::getNamespace,
        properties::setNamespace);
    setter.accept(properties);
  }

  private void readDefaultValue(String key, Supplier<String> getter, Consumer<String> setter) {
    String currentValue = getter.get();
    if (currentValue != null) {
      LOG.debug("Converter property {} already set", key);
      return;
    }
    LOG.debug("Reading converter property {}", key);
    String value = PROPERTIES.getProperty(key);
    if (value == null) {
      throw new RuntimeException(
          "No property found for key '" + key + "' while reading default values");
    }
    setter.accept(value);
  }
}
