package org.camunda.community.migration.converter.version;

public enum SemanticVersion {
  _8_0_0("8.0.0"),
  _8_1_0("8.1.0"),
  _8_2_0("8.2.0"),
  _8_3_0("8.3.0");

  private final String name;

  SemanticVersion(String name) {
    this.name = name;
  }

  public static SemanticVersion parse(String platformVersion) {
    for (SemanticVersion v : values()) {
      if (v.toString().equals(platformVersion)) {
        return v;
      }
    }
    throw new IllegalStateException("Not a valid Platform version: " + platformVersion);
  }

  @Override
  public String toString() {
    return name;
  }
}
