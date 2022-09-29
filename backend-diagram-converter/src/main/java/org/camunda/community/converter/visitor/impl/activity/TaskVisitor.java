package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.TaskConvertible;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class TaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "task";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return false;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new TaskConvertible();
  }
}
