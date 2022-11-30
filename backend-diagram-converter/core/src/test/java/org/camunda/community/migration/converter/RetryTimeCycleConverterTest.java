package org.camunda.community.migration.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RetryTimeCycleConverterTest {

  @ParameterizedTest
  @CsvSource(
      value = {"R5/PT1S; 5", "PT3S, PT4S, PT5S; 3", "PT1S, PT10M; 2"},
      delimiterString = ";")
  void shouldConvertToRetries(String timecycle, int expected) {
    List<String> durations = RetryTimeCycleConverter.convert(timecycle);
    assertEquals(expected, durations.size());
  }

  @ParameterizedTest
  @CsvSource(
      value = {" difba,fbieubf", "RT5S", "3"},
      delimiterString = ";")
  void shouldThrowIfInvalidExpression(String timecycle) {
    assertThrows(IllegalStateException.class, () -> RetryTimeCycleConverter.convert(timecycle));
  }
}
