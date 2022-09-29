package org.camunda.community.converter.visitor.impl.event;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.IntermediateCatchEventConvertible;
import org.camunda.community.converter.visitor.AbstractEventVisitor;

public class IntermediateCatchEventVisitor extends AbstractEventVisitor {
  @Override
  public String localName() {
    return "intermediateCatchEvent";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new IntermediateCatchEventConvertible();
  }
}
