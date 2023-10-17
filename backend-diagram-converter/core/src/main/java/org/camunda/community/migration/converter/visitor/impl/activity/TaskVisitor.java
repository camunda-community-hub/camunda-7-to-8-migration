package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.TaskConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class TaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "task";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new TaskConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_2;
  }
}
