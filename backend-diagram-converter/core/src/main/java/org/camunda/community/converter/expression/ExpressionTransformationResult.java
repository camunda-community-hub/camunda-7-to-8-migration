package org.camunda.community.converter.expression;

public class ExpressionTransformationResult {
  private String oldExpression;
  private String newExpression;

  public String getOldExpression() {
    return oldExpression;
  }

  public void setOldExpression(String oldExpression) {
    this.oldExpression = oldExpression;
  }

  public String getNewExpression() {
    return newExpression;
  }

  public void setNewExpression(String newExpression) {
    this.newExpression = newExpression;
  }
}
