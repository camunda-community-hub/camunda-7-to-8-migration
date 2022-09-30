package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.convertible.ServiceTaskConvertible.ZeebeTaskDefinition;

public class ServiceTaskConversion extends AbstractTypedConversion<ServiceTaskConvertible> {

  private DomElement createTaskDefinition(
      DomDocument document, ZeebeTaskDefinition zeebeTaskDefinition) {
    DomElement taskDefinition = document.createElement(NamespaceUri.ZEEBE, "taskDefinition");
    taskDefinition.setAttribute("type", zeebeTaskDefinition.getType());
    return taskDefinition;
  }

  @Override
  protected void convertTyped(DomElement element, ServiceTaskConvertible convertible) {
    DomElement extensionElements = super.getExtensionElements(element);
    if (convertible.getZeebeTaskDefinition().getType() != null) {
      extensionElements.appendChild(
          createTaskDefinition(element.getDocument(), convertible.getZeebeTaskDefinition()));
    }
  }

  @Override
  protected Class<ServiceTaskConvertible> type() {
    return ServiceTaskConvertible.class;
  }
}
