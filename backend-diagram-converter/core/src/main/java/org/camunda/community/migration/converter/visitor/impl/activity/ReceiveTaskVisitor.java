package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ReceiveTaskConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class ReceiveTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "receiveTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ReceiveTaskConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
