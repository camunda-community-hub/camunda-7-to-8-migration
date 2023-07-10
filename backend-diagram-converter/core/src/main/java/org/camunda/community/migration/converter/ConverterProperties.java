package org.camunda.community.migration.converter;

public interface ConverterProperties {

  String getScriptHeader();

  String getResultVariableHeader();

  String getDefaultJobType();

  String getScriptJobType();

  String getResourceHeader();

  String getScriptFormatHeader();

  String getPlatformVersion();

  Boolean getDefaultJobTypeEnabled();

  Boolean getAppendDocumentation();
}
