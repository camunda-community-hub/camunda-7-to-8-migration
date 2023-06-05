package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractCatchEventConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractTimerExpressionVisitor;

public class TimeDateVisitor extends AbstractTimerExpressionVisitor {
  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    if (isStartEvent(context.getElement()) || isEventSubprocess(context.getElement())) {
      return SemanticVersion._8_0_0;
    }
    return null;
  }

  @Override
  public String localName() {
    return "timeDate";
  }

  @Override
  protected void setNewExpression(AbstractCatchEventConvertible convertible, String newExpression) {
    convertible.setTimeDateExpression(newExpression);
  }
}
