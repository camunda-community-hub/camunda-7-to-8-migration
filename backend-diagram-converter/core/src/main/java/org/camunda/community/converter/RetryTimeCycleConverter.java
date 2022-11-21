package org.camunda.community.converter;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryTimeCycleConverter {
  private static final Logger LOG = LoggerFactory.getLogger(RetryTimeCycleConverter.class);
  private static final Pattern PATTERN = Pattern.compile("R(\\d*)/");
  private static final Pattern DURATION_PATTERN = Pattern.compile("(P.*)");

  /**
   * Returns all durations of a given timecycle expression as list.
   *
   * <p>Example:
   *
   * <p>timecycle: <code>R3/PT5S</code>
   *
   * <p>returns <code>["PT5S","PT5S","PT5S"]</code>
   *
   * @param timecycle the timecycle expression from a BPMN failedJobRetryTimeCycle
   * @return a list containing all given durations from the timecycle
   */
  public static List<String> convert(String timecycle) {
    // check if more expressions given
    List<String> durations = new ArrayList<>();
    String[] expressions =
        Arrays.stream(timecycle.split(",")).map(String::trim).toArray(String[]::new);
    LOG.debug("Found expressions: ['{}']", String.join("','", expressions));
    for (String expression : expressions) {
      LOG.debug("Handling expression {}", expression);
      int retries = 0;
      // check if the expression has an R*/
      Matcher matcher = PATTERN.matcher(expression);
      while (matcher.find()) {
        retries = Integer.parseInt(matcher.group(1));
      }
      if (!matcher.lookingAt()) {
        retries++;
      }
      Matcher duration = DURATION_PATTERN.matcher(expression);
      int appliedRetries = 0;
      while (duration.find()) {
        String group = duration.group(1);
        try {
          Duration.parse(group);
        } catch (DateTimeParseException e) {
          throw new IllegalStateException(e);
        }
        LOG.debug("Duration is {}", group);
        for (int i = 0; i < retries; i++) {
          durations.add(group);
          appliedRetries++;
        }
      }
      LOG.debug("Retries are {}", appliedRetries);
    }
    if (durations.isEmpty()) {
      throw new IllegalStateException(
          "Did not find any valid duration expressions in '" + timecycle + "'");
    }
    return durations;
  }
}
