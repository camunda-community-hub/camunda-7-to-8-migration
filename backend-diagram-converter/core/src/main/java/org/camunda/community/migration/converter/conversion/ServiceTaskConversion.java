package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible.ZeebeTaskDefinition;

public class ServiceTaskConversion extends AbstractTypedConversion<ServiceTaskConvertible> {

  private DomElement createTaskDefinition(
      DomDocument document, ZeebeTaskDefinition zeebeTaskDefinition) {
    DomElement taskDefinition = document.createElement(ZEEBE, "taskDefinition");
    if (zeebeTaskDefinition.getType() != null) {
      taskDefinition.setAttribute(ZEEBE, "type", zeebeTaskDefinition.getType());
    }
    if (zeebeTaskDefinition.getRetries() != null) {
      taskDefinition.setAttribute(ZEEBE, "retries", zeebeTaskDefinition.getRetries().toString());
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
