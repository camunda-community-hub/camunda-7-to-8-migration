package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.AbstractProcessElementConvertible;
import org.camunda.community.migration.converter.convertible.AbstractProcessElementConvertible.ZeebeProperty;

public class ProcessElementConversion
    extends AbstractTypedConversion<AbstractProcessElementConvertible> {
  @Override
  protected Class<AbstractProcessElementConvertible> type() {
    return AbstractProcessElementConvertible.class;
  }

  @Override
  public final void convertTyped(
      DomElement element, AbstractProcessElementConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    if (convertible.getZeebeProperties() != null && !convertible.getZeebeProperties().isEmpty()) {
      extensionElements.appendChild(createProperties(element.getDocument(), convertible));
    }
  }

  private DomElement createProperties(
      DomDocument document, AbstractProcessElementConvertible convertible) {
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
