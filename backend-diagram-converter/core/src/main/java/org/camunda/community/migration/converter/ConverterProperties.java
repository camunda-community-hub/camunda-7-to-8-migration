package org.camunda.community.migration.converter;

public interface ConverterProperties {

  String getScriptHeader();

  String getResultVariableHeader();

  String getAdapterJobType();

  String getScriptJobType();

  String getResourceHeader();

  String getScriptFormatHeader();

  String getPlatformVersion();
}
