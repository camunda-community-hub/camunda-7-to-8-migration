package org.camunda.community.converter.visitor.impl.eventReference;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.SignalConvertible;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractEventReferenceVisitor;

public class SignalVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "signal";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new SignalConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }
}
