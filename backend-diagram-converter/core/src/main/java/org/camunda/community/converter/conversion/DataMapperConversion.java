package org.camunda.community.converter.conversion;

import static org.camunda.community.converter.BpmnElementFactory.*;

import java.util.Set;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible.ZeebeIoMapping;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible.ZeebeTaskHeader;

public class DataMapperConversion extends AbstractTypedConversion<AbstractDataMapperConvertible> {

  @Override
  protected Class<AbstractDataMapperConvertible> type() {
    return AbstractDataMapperConvertible.class;
  }

  @Override
  protected final void convertTyped(DomElement element, AbstractDataMapperConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);

    if (convertible.getZeebeIoMappings() != null && !convertible.getZeebeIoMappings().isEmpty()) {
      extensionElements.appendChild(
          createIoMappings(element.getDocument(), convertible.getZeebeIoMappings()));
    }
    if (convertible.getZeebeTaskHeaders() != null && !convertible.getZeebeTaskHeaders().isEmpty()) {
      extensionElements.appendChild(
          createTaskHeaders(element.getDocument(), convertible.getZeebeTaskHeaders()));
    }
  }

  private DomElement createTaskHeaders(
      DomDocument document, Set<ZeebeTaskHeader> zeebeTaskHeaders) {
    DomElement taskHeaders = document.createElement(NamespaceUri.ZEEBE, "taskHeaders");
    zeebeTaskHeaders.forEach(
        zeebeTaskHeader -> taskHeaders.appendChild(createTaskHeader(zeebeTaskHeader, document)));
    return taskHeaders;
  }

  private DomElement createTaskHeader(ZeebeTaskHeader zeebeTaskHeader, DomDocument document) {
    DomElement taskHeader = document.createElement(NamespaceUri.ZEEBE, "header");
    taskHeader.setAttribute("key", zeebeTaskHeader.getKey());
    taskHeader.setAttribute("value", zeebeTaskHeader.getValue());
    return taskHeader;
  }

  private DomElement createIoMappings(DomDocument document, Set<ZeebeIoMapping> zeebeIoMappings) {
    DomElement mappings = document.createElement(NamespaceUri.ZEEBE, "ioMapping");
    zeebeIoMappings.forEach(
        zeebeIoMapping -> mappings.appendChild(createIoMapping(zeebeIoMapping, document)));
    return mappings;
  }

  private DomElement createIoMapping(ZeebeIoMapping zeebeIoMapping, DomDocument document) {
    DomElement mapping =
        document.createElement(
            NamespaceUri.ZEEBE, zeebeIoMapping.getDirection().name().toLowerCase());
    mapping.setAttribute("source", zeebeIoMapping.getSource());
    mapping.setAttribute("target", zeebeIoMapping.getTarget());
    return mapping;
  }
}
