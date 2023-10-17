package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible;

public class CallActivityConversion extends AbstractTypedConversion<CallActivityConvertible> {

  private DomElement createCalledElement(
      DomDocument document, CallActivityConvertible convertible) {
    DomElement calledElement = document.createElement(NamespaceUri.ZEEBE, "calledElement");
    if (convertible.getZeebeCalledElement().getProcessId() != null) {
      calledElement.setAttribute("processId", convertible.getZeebeCalledElement().getProcessId());
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
