package org.camunda.community.migration.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.EscalationConvertible;

public class EscalationConversion extends AbstractTypedConversion<EscalationConvertible> {
  @Override
  protected Class<EscalationConvertible> type() {
    return EscalationConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, EscalationConvertible convertible) {
    element.setAttribute(NamespaceUri.BPMN, "escalationCode", convertible.getEscalationCode());
  }
}
