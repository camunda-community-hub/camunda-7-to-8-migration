package org.camunda.community.converter.version;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class VersionExtractorTest {

  @Test
  void shouldExtractPlatformVersion() {
    String version = "8.0.0";
    VersionExtractor extractor = new VersionExtractor();
    int[] extractedVersion = extractor.apply(version);
    assertThat(extractedVersion).containsExactly(8, 0, 0);
  }
}
