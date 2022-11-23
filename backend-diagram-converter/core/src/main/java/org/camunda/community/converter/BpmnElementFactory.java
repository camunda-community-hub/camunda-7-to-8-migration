package org.camunda.community.converter;

import org.camunda.bpm.model.xml.instance.DomElement;

public class BpmnElementFactory {
  public static DomElement getExtensionElements(DomElement element) {
    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("extensionElements"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement extensionElements =
                  element
                      .getDocument()
                      .createElement(
                          NamespaceUri.BPMN, createElementName(element, "extensionElements"));
              element.insertChildElementAfter(extensionElements, getDocumentation(element));
              return extensionElements;
            });
  }

  public static DomElement getDocumentation(DomElement element) {
    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("documentation"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement extensionElements =
                  element
                      .getDocument()
                      .createElement(
                          NamespaceUri.BPMN, createElementName(element, "documentation"));
              element.insertChildElementAfter(extensionElements, null);
              return extensionElements;
            });
  }

  public static DomElement getMultiInstanceLoopCharacteristics(DomElement element) {
    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("multiInstanceLoopCharacteristics"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement mil =
                  element
                      .getDocument()
                      .createElement(
                          NamespaceUri.BPMN,
                          createElementName(element, "multiInstanceLoopCharacteristics"));
              element.appendChild(mil);
              return mil;
            });
  }

  public static DomElement getCompletionCondition(DomElement element) {
    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("completionCondition"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement mil =
                  element
                      .getDocument()
                      .createElement(
                          NamespaceUri.BPMN, createElementName(element, "completionCondition"));
              mil.setAttribute(
                  NamespaceUri.XSI, "type", createElementName(element, "tFormalExpression"));
              element.appendChild(mil);
              return mil;
            });
  }

  public static DomElement getConditionExpression(DomElement element) {
    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("conditionExpression"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement conditionExpressionElement =
                  element
                      .getDocument()
                      .createElement(
                          NamespaceUri.BPMN, createElementName(element, "conditionExpression"));
              element.appendChild(conditionExpressionElement);
              conditionExpressionElement.setAttribute(
                  NamespaceUri.XSI, "type", createElementName(element, "tFormalExpression"));
              return conditionExpressionElement;
            });
  }

  private static String createElementName(DomElement element, String localName) {
    String prefix = element.getRootElement().getPrefix();
    if (prefix == null) {
      return localName;
    }
    return prefix + ":" + localName;
  }
}
