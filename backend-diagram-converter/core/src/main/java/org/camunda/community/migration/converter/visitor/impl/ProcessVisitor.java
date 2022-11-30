package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ProcessConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractProcessElementVisitor;

public class ProcessVisitor extends AbstractProcessElementVisitor {
  @Override
  public String localName() {
    return "process";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ProcessConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
