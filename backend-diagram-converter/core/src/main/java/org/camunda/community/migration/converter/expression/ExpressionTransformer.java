package org.camunda.community.migration.converter.expression;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExpressionTransformer {
  private static final ExpressionTransformer INSTANCE = new ExpressionTransformer();

  private ExpressionTransformer() {}

  public static ExpressionTransformationResult transform(final String expression) {
    if (expression == null) {
      return null;
    }
    String transform = INSTANCE.doTransform(expression);
    ExpressionTransformationResult result = new ExpressionTransformationResult();
    result.setOldExpression(expression);
    result.setNewExpression(transform);
    return result;
  }

  private String doTransform(final String expression) {
    if (expression == null) {
      return null;
    }
    // split into expressions and non-expressions
    List<String> nonExpressions =
        Arrays.stream(expression.split("(#|\\$)\\{.*}"))
            .map(String::trim)
            .filter(s -> s.length() > 0)
            .collect(Collectors.toList());
    if (nonExpressions.size() == 1
        && expression.trim().length() == nonExpressions.get(0).length()) {
      return expression;
    }
    List<String> expressions =
        Arrays.stream(expression.split("(#|\\$)\\{|}"))
            .map(String::trim)
            .filter(s -> s.length() > 0)
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
            .replaceAll(" \\|\\| ", " or ");
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
