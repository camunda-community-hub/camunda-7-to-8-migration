package org.camunda.community.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RetryTimeCycleConverterTest {

  @ParameterizedTest
  @CsvSource(
      value = {"R5/PT1S; 5", "PT3S, PT4S, PT5S; 3", "R5/PT1S, PT10M; 6"},
      delimiterString = ";")
  void shouldConvertToRetries(String timecycle, int expected) {
    int retries = RetryTimeCycleConverter.convert(timecycle);
    assertEquals(expected, retries);
  }
}
