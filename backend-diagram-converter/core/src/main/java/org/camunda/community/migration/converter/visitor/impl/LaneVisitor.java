package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.LaneConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractProcessElementVisitor;

public class LaneVisitor extends AbstractProcessElementVisitor {
  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  public String localName() {
    return "lane";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new LaneConvertible();
  }
}
