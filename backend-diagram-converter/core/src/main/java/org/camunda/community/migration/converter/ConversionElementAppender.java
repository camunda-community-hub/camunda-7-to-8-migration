package org.camunda.community.migration.converter;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import java.util.Comparator;
import java.util.List;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DiagramCheckResult.ElementCheckMessage;

public class ConversionElementAppender {

  public void appendMessages(DomElement element, List<ElementCheckMessage> messages) {
    if (!messages.isEmpty()) {
      DomElement extensionElements = getExtensionElements(element);
      messages.sort(Comparator.comparingInt(message -> message.getSeverity().ordinal()));
      messages.forEach(
          message -> extensionElements.appendChild(createMessage(message, element.getDocument())));
    }
  }

  public void appendDocumentation(DomElement element, List<ElementCheckMessage> messages) {
    DomElement documentation = getDocumentation(element);
    documentation.setTextContent(createDocumentation(documentation.getTextContent(), messages));
  }

  public void appendReferences(DomElement element, List<String> references) {
    if (!references.isEmpty()) {
      DomElement extensionElements = getExtensionElements(element);
      references.forEach(
          reference ->
              extensionElements.appendChild(createReference(reference, element.getDocument())));
    }
  }

  public void appendReferencedBy(DomElement element, List<String> referencedBys) {
    if (!referencedBys.isEmpty()) {
      DomElement extensionElements = getExtensionElements(element);
      referencedBys.forEach(
          referencedBy ->
              extensionElements.appendChild(
                  createReferencedBy(referencedBy, element.getDocument())));
    }
  }

  private String createDocumentation(
      String currentDocumentation, List<ElementCheckMessage> messages) {
    StringBuilder documentation =
        new StringBuilder(currentDocumentation == null ? "" : currentDocumentation);
    if (currentDocumentation != null && currentDocumentation.trim().length() > 0) {
      documentation.append("\n\n");
    }
    messages.forEach(message -> documentation.append(formatMessage(message)));
    return documentation.toString();
  }

  private String formatMessage(ElementCheckMessage message) {
    return "- " + message.getSeverity() + ": " + message.getMessage() + "\n";
  }

  private DomElement createMessage(ElementCheckMessage message, DomDocument document) {
    DomElement messageElement = document.createElement(NamespaceUri.CONVERSION, "message");
    messageElement.setAttribute("severity", message.getSeverity().name());
    if (message.getLink() != null) {
      messageElement.setAttribute("link", message.getLink());
    }
    messageElement.setTextContent(message.getMessage());
    return messageElement;
  }

  private DomElement createReference(String reference, DomDocument document) {
    DomElement referenceElement = document.createElement(NamespaceUri.CONVERSION, "reference");
    referenceElement.setTextContent(reference);
    return referenceElement;
  }

  private DomElement createReferencedBy(String referencedBy, DomDocument document) {
    DomElement referenceElement = document.createElement(NamespaceUri.CONVERSION, "referencedBy");
    referenceElement.setTextContent(referencedBy);
    return referenceElement;
  }
}
