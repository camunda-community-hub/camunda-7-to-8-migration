package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.CallActivityConvertible;

public class CallActivityConversion extends AbstractTypedConversion<CallActivityConvertible> {

  private DomElement createCalledElement(
      DomDocument document, CallActivityConvertible convertible, ConverterProperties properties) {
    DomElement calledElement =
        document.createElement(properties.getZeebeNamespace().getUri(), "calledElement");
    if (convertible.getZeebeCalledElement().getProcessId() != null) {
      calledElement.setAttribute("processId", convertible.getZeebeCalledElement().getProcessId());
    }
    calledElement.setAttribute(
        "propagateAllChildVariables",
        Boolean.toString(convertible.getZeebeCalledElement().isPropagateAllChildVariables()));
    return calledElement;
  }

  @Override
  protected void convertTyped(
      DomElement element, CallActivityConvertible convertible, ConverterProperties properties) {
    DomElement extensionProperties = getExtensionElements(element, properties);
    extensionProperties.appendChild(
        createCalledElement(element.getDocument(), convertible, properties));
  }

  @Override
  protected Class<CallActivityConvertible> type() {
    return CallActivityConvertible.class;
  }
}
