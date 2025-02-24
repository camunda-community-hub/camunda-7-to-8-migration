package org.camunda.community.migration.converter.expression;

public class ExpressionTransformationResult {
  private final String context;
  private final String juelExpression;
  private final String feelExpression;
  private final Boolean hasMethodInvocation;
  private final Boolean hasExecutionOnly;

  public ExpressionTransformationResult(
      String context,
      String juelExpression,
      String feelExpression,
      Boolean hasMethodInvocation,
      Boolean hasExecutionOnly) {
    this.context = context;
    this.juelExpression = juelExpression;
    this.feelExpression = feelExpression;
    this.hasMethodInvocation = hasMethodInvocation;
    this.hasExecutionOnly = hasExecutionOnly;
  }

  public String getJuelExpression() {
    return juelExpression;
  }

  public String getFeelExpression() {
    return feelExpression;
  }

  public String getContext() {
    return context;
  }

  public Boolean getHasMethodInvocation() {
    return hasMethodInvocation;
  }

  public Boolean getHasExecutionOnly() {
    return hasExecutionOnly;
  }
}
