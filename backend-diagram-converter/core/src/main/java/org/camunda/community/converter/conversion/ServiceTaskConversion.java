package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.convertible.ServiceTaskConvertible.ZeebeTaskDefinition;

public class ServiceTaskConversion extends AbstractTypedConversion<ServiceTaskConvertible> {

  private DomElement createTaskDefinition(
      DomDocument document,
      ZeebeTaskDefinition zeebeTaskDefinition,
      ConverterProperties properties) {
    DomElement taskDefinition =
        document.createElement(properties.getZeebeNamespace().getUri(), "taskDefinition");
    taskDefinition.setAttribute("type", zeebeTaskDefinition.getType());
    return taskDefinition;
  }

  @Override
  protected void convertTyped(
      DomElement element, ServiceTaskConvertible convertible, ConverterProperties properties) {
    DomElement extensionElements = super.getExtensionElements(element, properties);
    if (convertible.getZeebeTaskDefinition().getType() != null) {
      extensionElements.appendChild(
          createTaskDefinition(
              element.getDocument(), convertible.getZeebeTaskDefinition(), properties));
    }
  }

  @Override
  protected Class<ServiceTaskConvertible> type() {
    return ServiceTaskConvertible.class;
  }
}
