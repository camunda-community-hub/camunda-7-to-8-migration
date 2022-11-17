package org.camunda.community.converter.conversion;

import java.util.Set;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible.ZeebeIoMapping;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible.ZeebeTaskHeader;

public class DataMapperConversion extends AbstractTypedConversion<AbstractDataMapperConvertible> {

  @Override
  protected Class<AbstractDataMapperConvertible> type() {
    return AbstractDataMapperConvertible.class;
  }

  @Override
  protected final void convertTyped(
      DomElement element,
      AbstractDataMapperConvertible convertible,
      ConverterProperties properties) {
    DomElement extensionElements = getExtensionElements(element, properties);

    if (convertible.getZeebeIoMappings() != null && !convertible.getZeebeIoMappings().isEmpty()) {
      extensionElements.appendChild(
          createIoMappings(element.getDocument(), convertible.getZeebeIoMappings(), properties));
    }
    if (convertible.getZeebeTaskHeaders() != null && !convertible.getZeebeTaskHeaders().isEmpty()) {
      extensionElements.appendChild(
          createTaskHeaders(element.getDocument(), convertible.getZeebeTaskHeaders(), properties));
    }
  }

  private DomElement createTaskHeaders(
      DomDocument document, Set<ZeebeTaskHeader> zeebeTaskHeaders, ConverterProperties properties) {
    DomElement taskHeaders =
        document.createElement(properties.getZeebeNamespace().getUri(), "taskHeaders");
    zeebeTaskHeaders.forEach(
        zeebeTaskHeader ->
            taskHeaders.appendChild(createTaskHeader(zeebeTaskHeader, document, properties)));
    return taskHeaders;
  }

  private DomElement createTaskHeader(
      ZeebeTaskHeader zeebeTaskHeader, DomDocument document, ConverterProperties properties) {
    DomElement taskHeader =
        document.createElement(properties.getZeebeNamespace().getUri(), "header");
    taskHeader.setAttribute("key", zeebeTaskHeader.getKey());
    taskHeader.setAttribute("value", zeebeTaskHeader.getValue());
    return taskHeader;
  }

  private DomElement createIoMappings(
      DomDocument document, Set<ZeebeIoMapping> zeebeIoMappings, ConverterProperties properties) {
    DomElement mappings =
        document.createElement(properties.getZeebeNamespace().getUri(), "ioMapping");
    zeebeIoMappings.forEach(
        zeebeIoMapping ->
            mappings.appendChild(createIoMapping(zeebeIoMapping, document, properties)));
    return mappings;
  }

  private DomElement createIoMapping(
      ZeebeIoMapping zeebeIoMapping, DomDocument document, ConverterProperties properties) {
    DomElement mapping =
        document.createElement(
            properties.getZeebeNamespace().getUri(),
            zeebeIoMapping.getDirection().name().toLowerCase());
    mapping.setAttribute("source", zeebeIoMapping.getSource());
    mapping.setAttribute("target", zeebeIoMapping.getTarget());
    return mapping;
  }
}
