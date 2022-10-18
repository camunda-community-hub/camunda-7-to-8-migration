package org.camunda.community.converter;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;

public class MessageAppender {
  public void appendMessages(
      DomElement element, List<BpmnElementCheckMessage> messages, boolean appendDocumentation) {
    if (!messages.isEmpty()) {
      DomElement extensionElements = getExtensionElements(element);
      messages.forEach(
          message -> extensionElements.appendChild(createMessage(message, element.getDocument())));
      if (appendDocumentation) {
        DomElement documentation = getDocumentation(element);
        documentation.setTextContent(createDocumentation(documentation.getTextContent(), messages));
      }
    }
  }

  private String createDocumentation(
      String currentDocumentation, List<BpmnElementCheckMessage> messages) {
    StringBuilder documentation =
        new StringBuilder(currentDocumentation == null ? "" : currentDocumentation);
    if (currentDocumentation != null && currentDocumentation.trim().length() > 0) {
      documentation.append("\n\n");
    }
    messages.forEach(message -> documentation.append(formatMessage(message)));
    return documentation.toString();
  }

  private String formatMessage(BpmnElementCheckMessage message) {
    return "- " + message.getSeverity() + ": " + message.getMessage() + "\n";
  }

  private DomElement createMessage(BpmnElementCheckMessage message, DomDocument document) {
    DomElement messageElement = document.createElement(NamespaceUri.CONVERSION, "message");
    messageElement.setAttribute("severity", message.getSeverity().name());
    messageElement.setTextContent(message.getMessage());
    return messageElement;
  }

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

  protected DomElement getDocumentation(DomElement element) {
    return element.getChildElements().stream()
        .filter(e -> e.getLocalName().equals("documentation"))
        .findFirst()
        .orElseGet(
            () -> {
              DomElement extensionElements =
                  element.getDocument().createElement(NamespaceUri.BPMN, "bpmn:documentation");
              element.insertChildElementAfter(extensionElements, null);
              return extensionElements;
            });
  }
}
