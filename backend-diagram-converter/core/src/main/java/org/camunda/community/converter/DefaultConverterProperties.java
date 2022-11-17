package org.camunda.community.converter;

import java.util.function.Consumer;

public class DefaultConverterProperties implements ConverterProperties {
  private ZeebeHeaderProperties scriptHeader;
  private ZeebeHeaderProperties resultVariableHeader;
  private ZeebeJobTypeProperties adapterJobType;
  private ZeebeJobTypeProperties scriptJobType;
  private ZeebeHeaderProperties resourceHeader;
  private ZeebeHeaderProperties scriptFormatHeader;
  private NamespaceProperties zeebeNamespace;
  private NamespaceProperties conversionNamespace;
  private NamespaceProperties modelerNamespace;
  private NamespaceProperties camundaNamespace;
  private NamespaceProperties bpmnNamespace;
  private NamespaceProperties xsiNamespace;
  private AttributeProperties executionPlatformAttribute;
  private AttributeProperties executionPlatformVersionAttribute;

  public static void setZeebeHeader(String name, Consumer<ZeebeHeaderProperties> setter) {
    if (name == null) {
      return;
    }
    DefaultZeebeHeaderProperties header = new DefaultZeebeHeaderProperties();
    header.setName(name);
    setter.accept(header);
  }

  public static void setZeebeJobType(String type, Consumer<ZeebeJobTypeProperties> setter) {
    if (type == null) {
      return;
    }
    DefaultZeebeJobTypeProperties jobType = new DefaultZeebeJobTypeProperties();
    jobType.setType(type);
    setter.accept(jobType);
  }

  @Override
  public ZeebeJobTypeProperties getScriptJobType() {
    return scriptJobType;
  }

  public void setScriptJobType(ZeebeJobTypeProperties scriptJobType) {
    this.scriptJobType = scriptJobType;
  }

  @Override
  public ZeebeHeaderProperties getResourceHeader() {
    return resourceHeader;
  }

  public void setResourceHeader(ZeebeHeaderProperties resourceHeader) {
    this.resourceHeader = resourceHeader;
  }

  @Override
  public ZeebeHeaderProperties getScriptFormatHeader() {
    return scriptFormatHeader;
  }

  public void setScriptFormatHeader(ZeebeHeaderProperties scriptFormatHeader) {
    this.scriptFormatHeader = scriptFormatHeader;
  }

  @Override
  public ZeebeHeaderProperties getScriptHeader() {
    return scriptHeader;
  }

  public void setScriptHeader(ZeebeHeaderProperties scriptHeader) {
    this.scriptHeader = scriptHeader;
  }

  @Override
  public ZeebeHeaderProperties getResultVariableHeader() {
    return resultVariableHeader;
  }

  public void setResultVariableHeader(ZeebeHeaderProperties resultVariableHeader) {
    this.resultVariableHeader = resultVariableHeader;
  }

  @Override
  public ZeebeJobTypeProperties getAdapterJobType() {
    return adapterJobType;
  }

  public void setAdapterJobType(ZeebeJobTypeProperties adapterJobType) {
    this.adapterJobType = adapterJobType;
  }

  @Override
  public NamespaceProperties getZeebeNamespace() {
    return zeebeNamespace;
  }

  public void setZeebeNamespace(NamespaceProperties zeebeNamespace) {
    this.zeebeNamespace = zeebeNamespace;
  }

  @Override
  public NamespaceProperties getConversionNamespace() {
    return conversionNamespace;
  }

  public void setConversionNamespace(NamespaceProperties conversionNamespace) {
    this.conversionNamespace = conversionNamespace;
  }

  @Override
  public NamespaceProperties getModelerNamespace() {
    return modelerNamespace;
  }

  public void setModelerNamespace(NamespaceProperties modelerNamespace) {
    this.modelerNamespace = modelerNamespace;
  }

  @Override
  public NamespaceProperties getCamundaNamespace() {
    return camundaNamespace;
  }

  public void setCamundaNamespace(NamespaceProperties camundaNamespace) {
    this.camundaNamespace = camundaNamespace;
  }

  @Override
  public NamespaceProperties getBpmnNamespace() {
    return bpmnNamespace;
  }

  public void setBpmnNamespace(NamespaceProperties bpmnNamespace) {
    this.bpmnNamespace = bpmnNamespace;
  }

  @Override
  public NamespaceProperties getXsiNamespace() {
    return xsiNamespace;
  }

  public void setXsiNamespace(NamespaceProperties xsiNamespace) {
    this.xsiNamespace = xsiNamespace;
  }

  @Override
  public AttributeProperties getExecutionPlatformAttribute() {
    return executionPlatformAttribute;
  }

  public void setExecutionPlatformAttribute(AttributeProperties executionPlatformAttribute) {
    this.executionPlatformAttribute = executionPlatformAttribute;
  }

  @Override
  public AttributeProperties getExecutionPlatformVersionAttribute() {
    return executionPlatformVersionAttribute;
  }

  public void setExecutionPlatformVersionAttribute(
      AttributeProperties executionPlatformVersionAttribute) {
    this.executionPlatformVersionAttribute = executionPlatformVersionAttribute;
  }

  public static class DefaultNamespaceProperties implements NamespaceProperties {
    private String name;
    private String uri;

    @Override
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }
  }

  public static class DefaultAttributeProperties implements AttributeProperties {
    private String name;
    private String value;
    private String namespace;

    @Override
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public String getNamespace() {
      return namespace;
    }

    public void setNamespace(String namespace) {
      this.namespace = namespace;
    }
  }

  public static class DefaultZeebeHeaderProperties implements ZeebeHeaderProperties {
    private String name;

    @Override
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class DefaultZeebeJobTypeProperties implements ZeebeJobTypeProperties {
    private String type;

    @Override
    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
