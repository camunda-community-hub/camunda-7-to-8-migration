package org.camunda.community.migration.converter;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import java.util.Comparator;
import java.util.List;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.BpmnDiagramCheckResult.BpmnElementCheckMessage;

public class MessageAppender {

  public void appendMessages(
      DomElement element, List<BpmnElementCheckMessage> messages, boolean appendDocumentation) {
    if (!messages.isEmpty()) {
      DomElement extensionElements = getExtensionElements(element);
      messages.sort(Comparator.comparingInt(message -> message.getSeverity().ordinal()));
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
    if (message.getLink() != null) {
      messageElement.setAttribute("link", message.getLink());
    }
    messageElement.setTextContent(message.getMessage());
    return messageElement;
  }
}
