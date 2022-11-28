package org.camunda.community.converter.version;

import java.util.Comparator;

public class VersionComparator implements Comparator<int[]> {

  @Override
  public int compare(int[] version1, int[] version2) {
    int positionsToCompare = Math.min(version1.length, version2.length);
    int comparison = 0;
    for (int i = 0; i < positionsToCompare; i++) {
      comparison = version1[i] - version2[i];
      if (comparison != 0) {
        return comparison;
      }
    }
    return comparison;
  }
}
