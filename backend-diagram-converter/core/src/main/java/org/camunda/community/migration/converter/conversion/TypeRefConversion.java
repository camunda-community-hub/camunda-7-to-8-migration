package org.camunda.community.migration.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.AbstractTypeRefConvertible;

public class TypeRefConversion extends AbstractTypedConversion<AbstractTypeRefConvertible> {
  @Override
  protected Class<AbstractTypeRefConvertible> type() {
    return AbstractTypeRefConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, AbstractTypeRefConvertible convertible) {
    if (convertible.getTypeRef() != null) {
      element.setAttribute("typeRef", convertible.getTypeRef());
    }
  }
}
