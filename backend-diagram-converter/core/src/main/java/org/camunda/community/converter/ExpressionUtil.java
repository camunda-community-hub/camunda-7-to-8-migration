package org.camunda.community.converter;

import java.util.Optional;

public class ExpressionUtil {
  private static final ExpressionUtil INSTANCE = new ExpressionUtil();

  private ExpressionUtil() {}

  public static Optional<String> transform(String possibleExpression, boolean createExpression) {
    if (possibleExpression == null) {
      return Optional.empty();
    }
    if (isExpression(possibleExpression)) {
      return INSTANCE.transformExpression(possibleExpression);
    }
    if (createExpression) {
      return Optional.of("=" + possibleExpression);
    }
    return Optional.of(possibleExpression);
  }

  public static boolean isExpression(String textContent) {
    return (textContent.startsWith("${") || textContent.startsWith("#{"))
        && textContent.endsWith("}");
  }

  public static boolean isTraversingExpression(String expression) {
    return isExpression(expression)
        && !INSTANCE.getUndeclaredExpression(expression).contains("(")
        && !INSTANCE.getUndeclaredExpression(expression).contains(")");
  }

  private Optional<String> transformExpression(String expression) {
    String undeclaredExpression = getUndeclaredExpression(expression);
    if (isTraversingExpression(expression) && !isExpression(undeclaredExpression)) {
      return Optional.of("=" + undeclaredExpression);
    }
    return Optional.empty();
  }

  private String getUndeclaredExpression(String expression) {
    return expression.substring(
        Math.max(expression.indexOf("${"), expression.indexOf("#{")) + 2,
        expression.lastIndexOf("}"));
  }
}
