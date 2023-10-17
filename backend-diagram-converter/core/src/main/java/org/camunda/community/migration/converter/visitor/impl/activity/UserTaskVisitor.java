package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class UserTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "userTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new UserTaskConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }
}
