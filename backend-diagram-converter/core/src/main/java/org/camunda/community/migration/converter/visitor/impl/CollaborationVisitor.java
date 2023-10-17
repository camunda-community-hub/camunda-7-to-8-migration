package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.CollaborationConvertible;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractProcessElementVisitor;

public class CollaborationVisitor extends AbstractProcessElementVisitor {

  @Override
  public String localName() {
    return "collaboration";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new CollaborationConvertible();
  }
}
