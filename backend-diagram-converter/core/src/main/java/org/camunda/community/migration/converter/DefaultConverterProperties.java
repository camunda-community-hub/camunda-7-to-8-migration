package org.camunda.community.migration.converter;

public class DefaultConverterProperties implements ConverterProperties {
  private String scriptHeader;
  private String resultVariableHeader;
  private String defaultJobType;
  private String scriptJobType;
  private String resourceHeader;
  private String scriptFormatHeader;
  private String platformVersion;
  private Boolean defaultJobTypeEnabled;
  private Boolean appendDocumentation;

  @Override
  public Boolean getAppendDocumentation() {
    return appendDocumentation;
  }

  public void setAppendDocumentation(Boolean appendDocumentation) {
    this.appendDocumentation = appendDocumentation;
  }

  @Override
  public Boolean getDefaultJobTypeEnabled() {
    return defaultJobTypeEnabled;
  }

  public void setDefaultJobTypeEnabled(Boolean defaultJobTypeEnabled) {
    this.defaultJobTypeEnabled = defaultJobTypeEnabled;
  }

  @Override
  public String getPlatformVersion() {
    return platformVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  @Override
  public String getScriptHeader() {
    return scriptHeader;
  }

  public void setScriptHeader(String scriptHeader) {
    this.scriptHeader = scriptHeader;
  }

  @Override
  public String getResultVariableHeader() {
    return resultVariableHeader;
  }

  public void setResultVariableHeader(String resultVariableHeader) {
    this.resultVariableHeader = resultVariableHeader;
  }

  @Override
  public String getDefaultJobType() {
    return defaultJobType;
  }

  public void setDefaultJobType(String defaultJobType) {
    this.defaultJobType = defaultJobType;
  }

  @Override
  public String getScriptJobType() {
    return scriptJobType;
  }

  public void setScriptJobType(String scriptJobType) {
    this.scriptJobType = scriptJobType;
  }

  @Override
  public String getResourceHeader() {
    return resourceHeader;
  }

  public void setResourceHeader(String resourceHeader) {
    this.resourceHeader = resourceHeader;
  }

  @Override
  public String getScriptFormatHeader() {
    return scriptFormatHeader;
  }

  public void setScriptFormatHeader(String scriptFormatHeader) {
    this.scriptFormatHeader = scriptFormatHeader;
  }
}
