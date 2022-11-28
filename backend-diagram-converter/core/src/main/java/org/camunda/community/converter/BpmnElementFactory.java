package org.camunda.community.converter;

import java.util.function.Consumer;
import org.camunda.bpm.model.xml.instance.DomElement;

public class BpmnElementFactory {

  private static DomElement getOrCreateBpmnElement(
      DomElement parent, String localName, Inserter inserter, Consumer<DomElement> childDecorator) {
    return parent.getChildElements().stream()
        .filter(e -> e.getLocalName().equals(localName))
        .filter(e -> e.getNamespaceURI().equals(NamespaceUri.BPMN))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement child =
                  parent
                      .getDocument()
                      .createElement(NamespaceUri.BPMN, createElementName(parent, localName));
              childDecorator.accept(child);
              inserter.insert(parent, child);
              return child;
            });
  }

  public static DomElement getExtensionElements(DomElement element) {
    return getOrCreateBpmnElement(
        element,
        "extensionElements",
        (parent, child) -> parent.insertChildElementAfter(child, getDocumentation(element)),
        child -> {});
  }

  public static DomElement getDocumentation(DomElement element) {
    return getOrCreateBpmnElement(
        element,
        "documentation",
        (parent, child) -> parent.insertChildElementAfter(child, null),
        child -> {});
  }

  public static DomElement getMultiInstanceLoopCharacteristics(DomElement element) {
    return getOrCreateBpmnElement(
        element, "multiInstanceLoopCharacteristics", DomElement::appendChild, child -> {});
  }

  public static DomElement getCompletionCondition(DomElement element) {
    return getOrCreateBpmnElement(
        element,
        "completionCondition",
        DomElement::appendChild,
        BpmnElementFactory::formalExpression);
  }

  public static DomElement getConditionExpression(DomElement element) {
    return getOrCreateBpmnElement(
        element,
        "conditionExpression",
        DomElement::appendChild,
        BpmnElementFactory::formalExpression);
  }

  private static void formalExpression(DomElement element) {
    element.setAttribute(NamespaceUri.XSI, "type", createElementName(element, "tFormalExpression"));
  }

  private static String createElementName(DomElement element, String localName) {
    String prefix = element.getRootElement().getPrefix();
    if (prefix == null) {
      return localName;
    }
    return prefix + ":" + localName;
  }

  private interface Inserter {
    void insert(DomElement parent, DomElement child);
  }
}
