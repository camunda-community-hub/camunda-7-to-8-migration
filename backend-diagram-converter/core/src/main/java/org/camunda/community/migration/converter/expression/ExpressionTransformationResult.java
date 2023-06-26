package org.camunda.community.migration.converter.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionTransformationResult {
  private static final Pattern methodInvocationPattern = Pattern.compile("\\.[\\w]*\\(.*\\)");
  private static final Pattern executionPattern = Pattern.compile("execution\\.");
  private final String juelExpression;
  private final String feelExpression;

  public ExpressionTransformationResult(String oldExpression, String newExpression) {
    this.juelExpression = oldExpression;
    this.feelExpression = newExpression;
  }

  public String getJuelExpression() {
    return juelExpression;
  }

  public String getFeelExpression() {
    return feelExpression;
  }

  public Boolean hasMethodInvocation() {
    Matcher m = methodInvocationPattern.matcher(juelExpression);
    return m.find();
  }

  public Boolean hasExecution() {
    Matcher m = executionPattern.matcher(juelExpression);
    return m.find();
  }
}
