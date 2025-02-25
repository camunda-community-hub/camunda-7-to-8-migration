package org.camunda.community.migration.converter.conversion;

import java.util.List;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.TextConvertible;

public class TextConversion extends AbstractTypedConversion<TextConvertible> {
  @Override
  protected Class<TextConvertible> type() {
    return TextConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, TextConvertible convertible) {
    List<DomElement> text = element.getChildElementsByNameNs(element.getNamespaceURI(), "text");
    if (text == null || text.isEmpty()) {
      throw new IllegalStateException(
          "No text elements found for element " + element.getLocalName());
    }
    text.get(0).setTextContent(convertible.getContent());
  }
}
