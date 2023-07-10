package org.camunda.community.migration.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
    readZeebeJobType("default", properties::getDefaultJobType, properties::setDefaultJobType);
    readZeebeJobType("script", properties::getScriptJobType, properties::setScriptJobType);
    readZeebeHeader("script", properties::getScriptHeader, properties::setScriptHeader);
    readZeebeHeader(
        "result-variable",
        properties::getResultVariableHeader,
        properties::setResultVariableHeader);
    readZeebeHeader("resource", properties::getResourceHeader, properties::setResourceHeader);
    readZeebeHeader(
        "script-format", properties::getScriptFormatHeader, properties::setScriptFormatHeader);
    readZeebePlatformInfo(
        "version", properties::getPlatformVersion, properties::setPlatformVersion);
    readFlag(
        "default-job-type-enabled",
        properties::getDefaultJobTypeEnabled,
        properties::setDefaultJobTypeEnabled);
    readFlag(
        "append-documentation",
        properties::getAppendDocumentation,
        properties::setAppendDocumentation);
  }

  private void readZeebeJobType(String jobType, Supplier<String> getter, Consumer<String> setter) {
    String key = String.join(DELIMITER, "zeebe-job-type", jobType);
    readDefaultValue(key, getter, setter, s -> s);
  }

  private void readZeebePlatformInfo(
      String info, Supplier<String> getter, Consumer<String> setter) {
    String key = String.join(DELIMITER, "zeebe-platform", info);
    readDefaultValue(key, getter, setter, s -> s);
  }

  private void readZeebeHeader(String header, Supplier<String> getter, Consumer<String> setter) {
    String key = String.join(DELIMITER, "zeebe-header", header);
    readDefaultValue(key, getter, setter, s -> s);
  }

  private void readFlag(String header, Supplier<Boolean> getter, Consumer<Boolean> setter) {
    String key = String.join(DELIMITER, "flag", header);
    readDefaultValue(key, getter, setter, Boolean::parseBoolean);
  }

  private <T> void readDefaultValue(
      String key, Supplier<T> getter, Consumer<T> setter, Function<String, T> mapper) {
    T currentValue = getter.get();
    if (currentValue != null) {
      LOG.debug("Converter property {} already set", key);
      return;
    }
    LOG.debug("Reading converter property {}", key);
    T value = mapper.apply(PROPERTIES.getProperty(key));
    if (value == null) {
      throw new RuntimeException(
          "No property found for key '" + key + "' while reading default values");
    }
    setter.accept(value);
  }
}
