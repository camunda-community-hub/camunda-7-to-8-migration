package org.camunda.community.migration.converter.visitor.impl.eventReference;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.SignalConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventReferenceVisitor;

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
    return SemanticVersion._8_2;
  }
}
