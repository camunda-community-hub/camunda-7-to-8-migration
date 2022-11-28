package org.camunda.community.converter.version;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class VersionComparatorTest {

  @Test
  void shouldFindEqualVersion() {
    int[] version1 = new int[] {8, 0, 0};
    int[] version2 = new int[] {8, 0};
    VersionComparator comparator = new VersionComparator();
    int compare = comparator.compare(version1, version2);
    assertThat(compare).isEqualTo(0);
  }

  @Test
  void shouldFindBiggerVersion() {
    int[] version1 = new int[] {8, 1, 0};
    int[] version2 = new int[] {8, 0};
    VersionComparator comparator = new VersionComparator();
    int compare = comparator.compare(version1, version2);
    assertThat(compare).isEqualTo(1);
  }

  @Test
  void shouldFindSmallerVersion() {
    int[] version1 = new int[] {0, 26, 0};
    int[] version2 = new int[] {8, 1};
    VersionComparator comparator = new VersionComparator();
    int compare = comparator.compare(version1, version2);
    assertThat(compare).isEqualTo(-8);
  }
}
