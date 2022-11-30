package org.camunda.community.migration.converter.visitor.impl.eventReference;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.EscalationConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventReferenceVisitor;

public class EscalationVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "escalation";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new EscalationConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }
}
