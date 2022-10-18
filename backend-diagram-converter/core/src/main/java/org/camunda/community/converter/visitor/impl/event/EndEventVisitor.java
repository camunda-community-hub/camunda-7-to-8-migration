package org.camunda.community.converter.visitor.impl.event;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.EndEventConvertible;
import org.camunda.community.converter.visitor.AbstractEventVisitor;

public class EndEventVisitor extends AbstractEventVisitor {
  @Override
  public String localName() {
    return "endEvent";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new EndEventConvertible();
  }
}
