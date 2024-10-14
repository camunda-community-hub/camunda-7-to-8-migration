package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible;

public class CallActivityConversion extends AbstractTypedConversion<CallActivityConvertible> {

  private DomElement createCalledElement(
      DomDocument document, CallActivityConvertible convertible) {
    DomElement calledElement = document.createElement(ZEEBE, "calledElement");
    if (convertible.getZeebeCalledElement().getProcessId() != null) {
      calledElement.setAttribute(
          ZEEBE, "processId", convertible.getZeebeCalledElement().getProcessId());
    }
    if (convertible.getZeebeCalledElement().getBindingType() != null) {
      calledElement.setAttribute(
          ZEEBE, "bindingType", convertible.getZeebeCalledElement().getBindingType().name());
    }
    if (StringUtils.isNotBlank(convertible.getZeebeCalledElement().getVersionTag())) {
      calledElement.setAttribute(
          ZEEBE, "versionTag", convertible.getZeebeCalledElement().getVersionTag());
    }
    calledElement.setAttribute(
        "propagateAllChildVariables",
        Boolean.toString(convertible.getZeebeCalledElement().isPropagateAllChildVariables()));
    calledElement.setAttribute(
        "propagateAllParentVariables",
        Boolean.toString(convertible.getZeebeCalledElement().isPropagateAllParentVariables()));
    return calledElement;
  }

  @Override
  protected void convertTyped(DomElement element, CallActivityConvertible convertible) {
    DomElement extensionProperties = getExtensionElements(element);
    extensionProperties.appendChild(createCalledElement(element.getDocument(), convertible));
  }

  @Override
  protected Class<CallActivityConvertible> type() {
    return CallActivityConvertible.class;
  }
}
