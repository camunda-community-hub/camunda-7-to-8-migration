package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class SendTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "sendTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ServiceTaskConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }
}
