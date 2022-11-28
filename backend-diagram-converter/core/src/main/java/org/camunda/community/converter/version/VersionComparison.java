package org.camunda.community.converter.version;

public class VersionComparison {
  private static final VersionComparator VERSION_COMPARATOR = new VersionComparator();
  private static final VersionExtractor VERSION_EXTRACTOR = new VersionExtractor();

  public static boolean isSupported(String actualVersion, String requiredVersion) {
    return VERSION_COMPARATOR.compare(
            VERSION_EXTRACTOR.apply(actualVersion), VERSION_EXTRACTOR.apply(requiredVersion))
        >= 0;
  }
}
