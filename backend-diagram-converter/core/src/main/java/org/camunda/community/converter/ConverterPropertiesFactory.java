package org.camunda.community.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.camunda.community.converter.ConverterProperties.AttributeProperties;
import org.camunda.community.converter.ConverterProperties.NamespaceProperties;
import org.camunda.community.converter.ConverterProperties.ZeebeHeaderProperties;
import org.camunda.community.converter.ConverterProperties.ZeebeJobTypeProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultAttributeProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultNamespaceProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultZeebeHeaderProperties;
import org.camunda.community.converter.DefaultConverterProperties.DefaultZeebeJobTypeProperties;
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
    readZeebeJobType("adapter", properties::getAdapterJobType, properties::setAdapterJobType);
    readZeebeJobType("script", properties::getScriptJobType, properties::setScriptJobType);
    readZeebeHeader("script", properties::getScriptHeader, properties::setScriptHeader);
    readZeebeHeader(
        "result-variable",
        properties::getResultVariableHeader,
        properties::setResultVariableHeader);
    readZeebeHeader("resource", properties::getResourceHeader, properties::setResourceHeader);
    readZeebeHeader(
        "script-format", properties::getScriptFormatHeader, properties::setScriptFormatHeader);
    readNamespaceProperties("zeebe", properties::getZeebeNamespace, properties::setZeebeNamespace);
    readNamespaceProperties(
        "conversion", properties::getConversionNamespace, properties::setConversionNamespace);
    readNamespaceProperties(
        "modeler", properties::getModelerNamespace, properties::setModelerNamespace);
    readNamespaceProperties(
        "camunda", properties::getCamundaNamespace, properties::setCamundaNamespace);
    readNamespaceProperties("bpmn", properties::getBpmnNamespace, properties::setBpmnNamespace);
    readNamespaceProperties("xsi", properties::getXsiNamespace, properties::setXsiNamespace);
    readAttributeProperties(
        "execution-platform",
        properties::getExecutionPlatformAttribute,
        properties::setExecutionPlatformAttribute);
    readAttributeProperties(
        "execution-platform-version",
        properties::getExecutionPlatformVersionAttribute,
        properties::setExecutionPlatformVersionAttribute);
  }

  private void readZeebeJobType(
      String jobType,
      Supplier<ZeebeJobTypeProperties> getter,
      Consumer<DefaultZeebeJobTypeProperties> setter) {
    String prefix = String.join(DELIMITER, "zeebe-job-type", jobType);
    DefaultZeebeJobTypeProperties properties =
        getOrCreate(
            getter, DefaultZeebeJobTypeProperties.class, DefaultZeebeJobTypeProperties::new);
    readDefaultValue(prefix, properties::getType, properties::setType);
    setter.accept(properties);
  }

  private <T> T getOrCreate(Supplier<? super T> getter, Class<T> clazz, Supplier<T> factory) {
    return Optional.ofNullable(getter.get())
        .filter(o -> clazz.isAssignableFrom(o.getClass()))
        .map(clazz::cast)
        .orElseGet(factory);
  }

  private void readZeebeHeader(
      String header,
      Supplier<ZeebeHeaderProperties> getter,
      Consumer<DefaultZeebeHeaderProperties> setter) {
    String prefix = String.join(DELIMITER, "zeebe-header", header);
    DefaultZeebeHeaderProperties properties =
        getOrCreate(getter, DefaultZeebeHeaderProperties.class, DefaultZeebeHeaderProperties::new);
    readDefaultValue(prefix, properties::getName, properties::setName);
    setter.accept(properties);
  }

  void readNamespaceProperties(
      String namespace,
      Supplier<NamespaceProperties> getter,
      Consumer<DefaultNamespaceProperties> setter) {
    String prefix = String.join(DELIMITER, "namespace", namespace);
    DefaultNamespaceProperties namespaceProperties =
        getOrCreate(getter, DefaultNamespaceProperties.class, DefaultNamespaceProperties::new);
    readDefaultValue(
        String.join(DELIMITER, prefix, "name"),
        namespaceProperties::getName,
        namespaceProperties::setName);
    readDefaultValue(
        String.join(DELIMITER, prefix, "uri"),
        namespaceProperties::getUri,
        namespaceProperties::setUri);
    setter.accept(namespaceProperties);
  }

  private void readAttributeProperties(
      String attribute,
      Supplier<AttributeProperties> getter,
      Consumer<DefaultAttributeProperties> setter) {
    String prefix = String.join(DELIMITER, "attribute", attribute);
    DefaultAttributeProperties properties =
        getOrCreate(getter, DefaultAttributeProperties.class, DefaultAttributeProperties::new);
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
