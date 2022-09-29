package org.camunda.community.converter.visitor.impl.eventReference;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.SignalConvertible;
import org.camunda.community.converter.visitor.AbstractEventReferenceVisitor;

public class SignalVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "signal";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return false;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new SignalConvertible();
  }
}
