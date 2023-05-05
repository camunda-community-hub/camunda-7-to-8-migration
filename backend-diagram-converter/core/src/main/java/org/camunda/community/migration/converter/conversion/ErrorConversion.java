package org.camunda.community.migration.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.ErrorConvertible;

public class ErrorConversion extends AbstractTypedConversion<ErrorConvertible> {
  @Override
  protected Class<ErrorConvertible> type() {
    return ErrorConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, ErrorConvertible convertible) {
    element.setAttribute(NamespaceUri.BPMN, "errorCode", convertible.getErrorCode());
  }
}
