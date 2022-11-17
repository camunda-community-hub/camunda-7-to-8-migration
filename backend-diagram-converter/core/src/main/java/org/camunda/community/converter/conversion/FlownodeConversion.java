package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.AbstractFlownodeConvertible;
import org.camunda.community.converter.convertible.AbstractFlownodeConvertible.ZeebeProperty;

// TODO extract local names from all conversions
public class FlownodeConversion extends AbstractTypedConversion<AbstractFlownodeConvertible> {
  @Override
  protected Class<AbstractFlownodeConvertible> type() {
    return AbstractFlownodeConvertible.class;
  }

  @Override
  public final void convertTyped(
      DomElement element, AbstractFlownodeConvertible convertible, ConverterProperties properties) {
    DomElement extensionElements = getExtensionElements(element, properties);
    if (convertible.getZeebeProperties() != null && !convertible.getZeebeProperties().isEmpty()) {
      extensionElements.appendChild(
          createProperties(element.getDocument(), convertible, properties));
    }
  }

  private DomElement createProperties(
      DomDocument document,
      AbstractFlownodeConvertible convertible,
      ConverterProperties converterProperties) {
    DomElement properties =
        document.createElement(converterProperties.getZeebeNamespace().getUri(), "properties");
    convertible
        .getZeebeProperties()
        .forEach(
            property ->
                properties.appendChild(createProperty(property, document, converterProperties)));
    return properties;
  }

  private DomElement createProperty(
      ZeebeProperty zeebeProperty, DomDocument document, ConverterProperties properties) {
    DomElement property =
        document.createElement(properties.getZeebeNamespace().getUri(), "property");
    if (zeebeProperty.getName() != null) {
      property.setAttribute("name", zeebeProperty.getName());
    }
    if (zeebeProperty.getValue() != null) {
      property.setAttribute("value", zeebeProperty.getValue());
    }
    return property;
  }
}
