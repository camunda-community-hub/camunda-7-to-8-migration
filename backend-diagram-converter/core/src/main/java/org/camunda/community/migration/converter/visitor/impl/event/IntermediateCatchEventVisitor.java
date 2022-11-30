package org.camunda.community.migration.converter.visitor.impl.event;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.IntermediateCatchEventConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventVisitor;

public class IntermediateCatchEventVisitor extends AbstractEventVisitor {
  @Override
  public String localName() {
    return "intermediateCatchEvent";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new IntermediateCatchEventConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
