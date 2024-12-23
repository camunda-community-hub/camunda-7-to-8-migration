package org.camunda.community.migration.converter.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionTransformationResult {

  private static final Logger LOG = LoggerFactory.getLogger(ExpressionTransformationResult.class);

  private static final Pattern methodInvocationPattern = Pattern.compile("\\.[\\w]*\\(.*\\)");
  private static final Pattern executionPattern = Pattern.compile("execution\\.");
  private static final Pattern executionGetVariablePattern =
      Pattern.compile("execution.getVariable");
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
    if (hasExecutionGetVariable()) {
      return false;
    }
    Matcher m = methodInvocationPattern.matcher(juelExpression);
    boolean methodMatch = m.find();
    LOG.debug("{} contains method invocation: {}", juelExpression, methodMatch);
    return methodMatch;
  }

  public Boolean hasExecutionOnly() {
    if (hasExecutionGetVariable()) {
      return false;
    }
    Matcher m = executionPattern.matcher(juelExpression);
    boolean executionOnlyMatch = m.find();
    LOG.debug("{} contains execution only: {}", juelExpression, executionOnlyMatch);
    return executionOnlyMatch;
  }

  public Boolean hasExecutionGetVariable() {
    Matcher m = executionGetVariablePattern.matcher(juelExpression);
    boolean executionGetVariableMatch = m.find();
    LOG.debug("{} contains execution.getVariable: {}", juelExpression, executionGetVariableMatch);
    return executionGetVariableMatch;
  }
}
