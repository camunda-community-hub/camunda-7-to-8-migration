package org.camunda.community.migration.converter.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionTransformationResult {
  private String oldExpression;
  private String newExpression;

  private static Pattern methodInvocationPattern = Pattern.compile("\\.[\\w]*\\(.*\\)");
  private static Pattern executionPattern = Pattern.compile("execution\\.");

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
    Matcher m = methodInvocationPattern.matcher(oldExpression);
    return m.find();
  }

  public Boolean hasExecution() {
    Matcher m = executionPattern.matcher(oldExpression);
    return m.find();
  }
}
