package org.camunda.community.converter.conversion;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.Convertible;

public abstract class AbstractTypedConversion<T extends Convertible> implements Conversion {

  @Override
  public final void convert(
      DomElement element, Convertible convertible, List<BpmnElementCheckMessage> messages) {
    if (type().isAssignableFrom(convertible.getClass())) {
      convertTyped(element, type().cast(convertible));
    }
    removeExtensionElementsIfEmpty(getExtensionElements(element));
  }

  private void removeExtensionElementsIfEmpty(DomElement extensionElements) {
    if (extensionElements.getChildElements().isEmpty()) {
      extensionElements.getParentElement().removeChild(extensionElements);
    }
  }

  protected abstract Class<T> type();

  protected abstract void convertTyped(DomElement element, T convertible);

  protected DomElement getExtensionElements(DomElement element) {

    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("extensionElements"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement extensionElements =
                  element.getDocument().createElement(NamespaceUri.BPMN, "bpmn:extensionElements");
              element.insertChildElementAfter(extensionElements, null);
              return extensionElements;
            });
  }
}
