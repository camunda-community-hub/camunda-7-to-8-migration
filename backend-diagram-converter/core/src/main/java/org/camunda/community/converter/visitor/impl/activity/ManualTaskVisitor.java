package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ManualTaskConvertible;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class ManualTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "manualTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ManualTaskConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }
}
