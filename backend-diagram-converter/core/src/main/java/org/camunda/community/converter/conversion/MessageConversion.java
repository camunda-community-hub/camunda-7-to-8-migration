package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.MessageConvertible;

public class MessageConversion extends AbstractTypedConversion<MessageConvertible> {

  private DomElement createSubscription(
      DomDocument document, MessageConvertible convertible, ConverterProperties properties) {
    DomElement subscription =
        document.createElement(properties.getZeebeNamespace().getUri(), "subscription");
    subscription.setAttribute(
        "correlationKey", convertible.getZeebeSubscription().getCorrelationKey());
    return subscription;
  }

  @Override
  protected void convertTyped(
      DomElement element, MessageConvertible convertible, ConverterProperties properties) {
    DomElement extensionElements = getExtensionElements(element, properties);
    if (convertible.getZeebeSubscription().getCorrelationKey() != null) {
      extensionElements.appendChild(
          createSubscription(element.getDocument(), convertible, properties));
    }
  }

  @Override
  protected Class<MessageConvertible> type() {
    return MessageConvertible.class;
  }
}
