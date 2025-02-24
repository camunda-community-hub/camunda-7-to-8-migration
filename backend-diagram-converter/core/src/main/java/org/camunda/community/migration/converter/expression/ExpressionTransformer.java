package org.camunda.community.migration.converter.expression;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionTransformer {
  private static final Logger LOG = LoggerFactory.getLogger(ExpressionTransformer.class);
  private static final ExpressionTransformer INSTANCE = new ExpressionTransformer();
  private static final Pattern methodInvocationPattern = Pattern.compile("\\.[\\w]*\\(.*\\)");
  private static final Pattern executionPattern = Pattern.compile("execution\\.");
  private static final Pattern executionGetVariablePattern =
      Pattern.compile("execution.getVariable");

  private ExpressionTransformer() {}

  public static ExpressionTransformationResult transform(
      final String context, final String juelExpression) {
    if (juelExpression == null) {
      return null;
    }
    String transform = INSTANCE.doTransform(juelExpression);
    boolean hasMethodInvocation = INSTANCE.hasMethodInvocation(juelExpression);
    boolean hasExecutionOnly = INSTANCE.hasExecutionOnly(juelExpression);
    return new ExpressionTransformationResult(
        context, juelExpression, transform, hasMethodInvocation, hasExecutionOnly);
  }

  private Boolean hasMethodInvocation(String juelExpression) {
    if (hasExecutionGetVariable(juelExpression)) {
      return false;
    }
    Matcher m = methodInvocationPattern.matcher(juelExpression);
    boolean methodMatch = m.find();
    LOG.debug("{} contains method invocation: {}", juelExpression, methodMatch);
    return methodMatch;
  }

  private Boolean hasExecutionOnly(String juelExpression) {
    if (hasExecutionGetVariable(juelExpression)) {
      return false;
    }
    Matcher m = executionPattern.matcher(juelExpression);
    boolean executionOnlyMatch = m.find();
    LOG.debug("{} contains execution only: {}", juelExpression, executionOnlyMatch);
    return executionOnlyMatch;
  }

  private Boolean hasExecutionGetVariable(String juelExpression) {
    Matcher m = executionGetVariablePattern.matcher(juelExpression);
    boolean executionGetVariableMatch = m.find();
    LOG.debug("{} contains execution.getVariable: {}", juelExpression, executionGetVariableMatch);
    return executionGetVariableMatch;
  }

  private String doTransform(final String juelExpression) {
    if (juelExpression == null) {
      return null;
    }
    if (juelExpression.isEmpty()) {
      return "=null";
    }
    // split into expressions and non-expressions
    List<String> nonExpressions =
        Arrays.stream(juelExpression.split("(#|\\$)\\{.*}"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    if (nonExpressions.size() == 1
        && juelExpression.trim().length() == nonExpressions.get(0).length()) {
      return juelExpression;
    }
    List<String> expressions =
        Arrays.stream(juelExpression.split("(#|\\$)\\{|}"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> nonExpressions.contains(s) ? "\"" + s + "\"" : handleExpression(s))
            .collect(Collectors.toList());
    return "=" + String.join(" + ", expressions);
  }

  private String handleExpression(String expression) {
    // replace operators globally
    String replaced =
        expression
            .replaceAll("empty (\\S*)", "$1=null")
            // replace all !x with not(x)
            .replaceAll("!(?![\\(=])([\\S-^]*)", "not($1)")
            // replace all not x with not(x)
            .replaceAll("not (?![\\(=])([\\S-^]*)", "not($1)")
            // replace all x["y"] with x.y
            .replaceAll("\\[\\\"(\\D[^\\]\\[]*)\\\"]", ".$1")
            .replaceAll(" gt ", " > ")
            .replaceAll(" lt ", " < ")
            .replaceAll("==", "=")
            .replaceAll(" eq ", " = ")
            .replaceAll(" ne ", " != ")
            // replace all !(x and y) with not(x and y)
            .replaceAll("!\\(([^\\(\\)]*)\\)", "not($1)")
            .replaceAll(" && ", " and ")
            .replaceAll(" \\|\\| ", " or ")
            .replaceAll("'", "\"")
            .replaceAll("execution\\.getVariable\\(\"(.*)\"\\)", "$1");
    // increment all indexes
    Pattern pattern = Pattern.compile("\\[(\\d*)\\]");
    Matcher m = pattern.matcher(replaced);
    while (m.find()) {
      String oldIndex = "[" + Long.parseLong(m.group(1)) + "]";
      String newIndex = "[" + (Long.parseLong(m.group(1)) + 1) + "]";
      replaced = replaced.replace(oldIndex, newIndex);
    }
    return replaced;
  }
}
