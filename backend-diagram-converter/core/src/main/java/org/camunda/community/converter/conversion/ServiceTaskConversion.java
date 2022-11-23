package org.camunda.community.converter.conversion;

import static org.camunda.community.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.convertible.ServiceTaskConvertible.ZeebeTaskDefinition;

public class ServiceTaskConversion extends AbstractTypedConversion<ServiceTaskConvertible> {

  private DomElement createTaskDefinition(
      DomDocument document, ZeebeTaskDefinition zeebeTaskDefinition) {
    DomElement taskDefinition = document.createElement(NamespaceUri.ZEEBE, "taskDefinition");
    if (zeebeTaskDefinition.getType() != null) {
      taskDefinition.setAttribute("type", zeebeTaskDefinition.getType());
    }
    if (zeebeTaskDefinition.getRetries() != null) {
      taskDefinition.setAttribute("retries", zeebeTaskDefinition.getRetries().toString());
    }
    return taskDefinition;
  }

  @Override
  protected void convertTyped(DomElement element, ServiceTaskConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (convertible.getZeebeTaskDefinition().getType() != null
        || convertible.getZeebeTaskDefinition().getRetries() != null) {
      extensionElements.appendChild(
          createTaskDefinition(element.getDocument(), convertible.getZeebeTaskDefinition()));
    }
  }

  @Override
  protected Class<ServiceTaskConvertible> type() {
    return ServiceTaskConvertible.class;
  }
}
