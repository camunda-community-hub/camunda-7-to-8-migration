package org.camunda.community.converter;

public class DefaultConverterProperties implements ConverterProperties {
  private String scriptHeader;
  private String resultVariableHeader;
  private String adapterJobType;
  private String scriptJobType;
  private String resourceHeader;
  private String scriptFormatHeader;
  private String platformVersion;

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
  public String getAdapterJobType() {
    return adapterJobType;
  }

  public void setAdapterJobType(String adapterJobType) {
    this.adapterJobType = adapterJobType;
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
