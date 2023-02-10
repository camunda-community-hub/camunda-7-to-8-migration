package org.camunda.community.migration.converter.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  public Boolean hasMethodInvocation() {
    Pattern pattern = Pattern.compile("[^\\s]\\.get.*\\(\\)");
    Matcher m = pattern.matcher(oldExpression);
    return m.find();
  }

  public Boolean hasExecution() {
    Pattern pattern = Pattern.compile("execution\\.");
    Matcher m = pattern.matcher(oldExpression);
    return m.find();
  }
}
