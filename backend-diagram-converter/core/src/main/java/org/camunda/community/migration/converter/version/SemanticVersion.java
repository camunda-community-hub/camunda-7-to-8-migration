package org.camunda.community.migration.converter.version;

public enum SemanticVersion {
  _8_0("8.0"),
  _8_1("8.1"),
  _8_2("8.2"),
  _8_3("8.3"),
  _8_4("8.4"),
  _8_5("8.5");

  private final String name;

  SemanticVersion(String name) {
    this.name = name;
  }

  public static SemanticVersion parse(String platformVersion) {
    platformVersion += ".";
    for (SemanticVersion v : values()) {
      if (platformVersion.startsWith(v.toString() + ".")) {
        return v;
      }
    }
    throw new IllegalStateException("Not a valid Platform version: " + platformVersion);
  }

  @Override
  public String toString() {
    return name;
  }

  public String getPatchZeroVersion() {
    return name + ".0";
  }
}
