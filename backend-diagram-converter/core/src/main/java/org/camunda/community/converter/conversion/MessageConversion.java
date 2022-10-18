package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.MessageConvertible;

public class MessageConversion extends AbstractTypedConversion<MessageConvertible> {

  private DomElement createSubscription(DomDocument document, MessageConvertible convertible) {
    DomElement subscription = document.createElement(NamespaceUri.ZEEBE, "subscription");
    subscription.setAttribute(
        "correlationKey", convertible.getZeebeSubscription().getCorrelationKey());
    return subscription;
  }

  @Override
  protected void convertTyped(DomElement element, MessageConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (convertible.getZeebeSubscription().getCorrelationKey() != null) {
      extensionElements.appendChild(createSubscription(element.getDocument(), convertible));
    }
  }

  @Override
  protected Class<MessageConvertible> type() {
    return MessageConvertible.class;
  }
}
