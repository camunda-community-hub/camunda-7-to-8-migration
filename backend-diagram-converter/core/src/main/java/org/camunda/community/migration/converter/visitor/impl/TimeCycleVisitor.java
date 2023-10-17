package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractCatchEventConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractTimerExpressionVisitor;

public class TimeCycleVisitor extends AbstractTimerExpressionVisitor {
  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    if (isStartEvent(context.getElement())) {
      return SemanticVersion._8_0;
    }
    return null;
  }

  @Override
  protected void setNewExpression(AbstractCatchEventConvertible convertible, String newExpression) {
    convertible.setTimeCycleExpression(newExpression);
  }

  @Override
  public String localName() {
    return "timeCycle";
  }
}
