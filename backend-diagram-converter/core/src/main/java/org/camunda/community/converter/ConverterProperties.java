package org.camunda.community.converter;

import java.util.concurrent.atomic.AtomicReference;

public interface ConverterProperties {

  static NamespaceProperties resolveNamespace(String name) {
    AtomicReference<NamespaceProperties> ref = new AtomicReference<>();
    ConverterPropertiesFactory.getInstance().readNamespaceProperties(name, ref::set);
    return ref.get();
  }

  ZeebeHeaderProperties getScriptHeader();

  ZeebeHeaderProperties getResultVariableHeader();

  ZeebeHeaderProperties getAdapterJobTypeHeader();

  ZeebeHeaderProperties getResourceHeader();

  ZeebeHeaderProperties getScriptFormatHeader();

  NamespaceProperties getZeebeNamespace();

  NamespaceProperties getConversionNamespace();

  NamespaceProperties getModelerNamespace();

  NamespaceProperties getCamundaNamespace();

  NamespaceProperties getBpmnNamespace();

  NamespaceProperties getXsiNamespace();

  AttributeProperties getExecutionPlatformAttribute();

  AttributeProperties getExecutionPlatformVersionAttribute();

  interface NamespaceProperties {
    String getName();

    String getUri();
  }

  interface AttributeProperties {
    String getNamespace();

    String getName();

    String getValue();
  }

  interface ZeebeHeaderProperties {
    String getName();
  }
}
