package org.camunda.community.converter.version;

import java.util.Arrays;
import java.util.function.Function;

public class VersionExtractor implements Function<String, int[]> {
  @Override
  public int[] apply(String version) {
    return Arrays.stream(version.split("\\.")).mapToInt(Integer::valueOf).toArray();
  }
}
