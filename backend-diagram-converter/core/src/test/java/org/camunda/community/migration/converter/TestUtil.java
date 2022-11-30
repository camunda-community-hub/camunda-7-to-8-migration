package org.camunda.community.migration.converter;

import java.util.HashMap;
import java.util.Map;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

public class TestUtil {
  public static Map<String, Object> mockVariables(int count) {
    Map<String, Object> variables = new HashMap<>();
    for (int i = 0; i < count; i++) {
      variables.put(RandomStringUtils.random(5), RandomStringUtils.random(20));
    }
    return variables;
  }
}
