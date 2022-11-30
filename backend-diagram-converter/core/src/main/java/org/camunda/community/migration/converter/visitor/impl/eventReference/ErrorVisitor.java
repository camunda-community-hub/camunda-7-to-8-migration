package org.camunda.community.migration.converter.visitor.impl.eventReference;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ErrorConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventReferenceVisitor;

public class ErrorVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "error";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ErrorConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
