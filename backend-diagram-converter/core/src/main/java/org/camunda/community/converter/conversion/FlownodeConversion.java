package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.AbstractFlownodeConvertible;
import org.camunda.community.converter.convertible.AbstractFlownodeConvertible.ZeebeProperty;

public class FlownodeConversion extends AbstractTypedConversion<AbstractFlownodeConvertible> {
  @Override
  protected Class<AbstractFlownodeConvertible> type() {
    return AbstractFlownodeConvertible.class;
  }

  @Override
  public final void convertTyped(DomElement element, AbstractFlownodeConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (convertible.getZeebeProperties() != null && !convertible.getZeebeProperties().isEmpty()) {
      extensionElements.appendChild(createProperties(element.getDocument(), convertible));
    }
  }

  private DomElement createProperties(
      DomDocument document, AbstractFlownodeConvertible convertible) {
    DomElement properties = document.createElement(NamespaceUri.ZEEBE, "properties");
    convertible
        .getZeebeProperties()
        .forEach(property -> properties.appendChild(createProperty(property, document)));
    return properties;
  }

  private DomElement createProperty(ZeebeProperty zeebeProperty, DomDocument document) {
    DomElement property = document.createElement(NamespaceUri.ZEEBE, "property");
    if (zeebeProperty.getName() != null) {
      property.setAttribute("name", zeebeProperty.getName());
    }
    if (zeebeProperty.getValue() != null) {
      property.setAttribute("value", zeebeProperty.getValue());
    }
    return property;
  }
}
