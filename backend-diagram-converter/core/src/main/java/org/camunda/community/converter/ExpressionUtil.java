package org.camunda.community.converter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExpressionUtil {
  private static final ExpressionUtil INSTANCE = new ExpressionUtil();

  private ExpressionUtil() {}

  public static String transform(String expression) {
    return INSTANCE.doTransform(expression);
  }

  private String doTransform(String expression) {
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
      System.out.println(expression);
      return expression;
    }
    List<String> expressions =
        Arrays.stream(expression.split("(#|\\$)\\{|}"))
            .map(String::trim)
            .filter(s -> s.length() > 0)
            .map(s -> nonExpressions.contains(s) ? "\"" + s + "\"" : handleExpression(s))
            .collect(Collectors.toList());
    String result = "=" + String.join(" + ", expressions);
    System.out.println(result);
    return result;
  }

  private String handleExpression(String expression) {
    // replace operators globally
    String replaced =
        expression
            .replaceAll("not ", "!")
            .replaceAll("\\[\"", ".")
            .replaceAll("\"\\]", "")
            .replaceAll(" gt ", " > ")
            .replaceAll(" lt ", " < ")
            .replaceAll("==", "=")
            .replaceAll(" eq ", " = ")
            .replaceAll(" ne ", " != ")
            .replaceAll("!\\(", "not(")
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
    // replace block-wise
    List<String> blocks =
        Arrays.stream(replaced.split(" "))
            .map(
                s ->
                    Pattern.compile("^!(?!=)").matcher(s).lookingAt()
                        ? s.replaceAll("^!(?!=)", "not(") + ")"
                        : s)
            .collect(Collectors.toList());
    // increment all explicit array element pointers

    System.out.println(blocks);
    return String.join(" ", blocks);
  }
}
