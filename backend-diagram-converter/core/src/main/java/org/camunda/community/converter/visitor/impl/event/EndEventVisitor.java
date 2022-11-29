package org.camunda.community.converter.visitor.impl.event;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.EndEventConvertible;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractEventVisitor;

public class EndEventVisitor extends AbstractEventVisitor {
  @Override
  public String localName() {
    return "endEvent";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new EndEventConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
