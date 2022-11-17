package org.camunda.community.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetryTimeCycleConverter {
  private static final Pattern PATTERN = Pattern.compile("R(\\d*)/");

  public static int convert(String timecycle) {
    // check if more expressions given
    int retries = 0;
    String[] expressions = timecycle.split(",");
    for (String expression : expressions) {
      // check if the expression has an R*/
      Matcher matcher = PATTERN.matcher(expression);
      while (matcher.find()) {
        retries += Integer.parseInt(matcher.group(1));
      }
      if (!matcher.lookingAt()) {
        retries++;
      }
    }
    return Math.max(1, retries);
  }
}
