package org.camunda.community.converter;

import java.util.concurrent.atomic.AtomicReference;
import org.camunda.community.converter.DefaultConverterProperties.DefaultNamespaceProperties;

public interface ConverterProperties {

  static NamespaceProperties resolveNamespace(String name) {
    AtomicReference<DefaultNamespaceProperties> ref = new AtomicReference<>();
    ConverterPropertiesFactory.getInstance().readNamespaceProperties(name, ref::get, ref::set);
    return ref.get();
  }

  ZeebeHeaderProperties getScriptHeader();

  ZeebeHeaderProperties getResultVariableHeader();

  ZeebeJobTypeProperties getAdapterJobType();

  ZeebeJobTypeProperties getScriptJobType();

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

  interface ZeebeJobTypeProperties {
    String getType();
  }
}
