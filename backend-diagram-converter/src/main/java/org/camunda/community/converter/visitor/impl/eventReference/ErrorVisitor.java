package org.camunda.community.converter.visitor.impl.eventReference;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ErrorConvertible;
import org.camunda.community.converter.visitor.AbstractEventReferenceVisitor;

public class ErrorVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "error";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ErrorConvertible();
  }
}
