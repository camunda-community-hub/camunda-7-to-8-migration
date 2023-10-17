package org.camunda.community.migration.converter.visitor.impl.event;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.BoundaryEventConvertible;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventVisitor;

public class BoundaryEventVisitor extends AbstractEventVisitor {
  @Override
  public String localName() {
    return "boundaryEvent";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new BoundaryEventConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }
}
