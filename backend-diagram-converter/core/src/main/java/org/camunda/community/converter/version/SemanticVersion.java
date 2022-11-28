package org.camunda.community.converter.version;

public enum SemanticVersion {
  _8_0_0("8.0.0"),
  _8_1_0("8.1.0"),
  _8_2_0("8.2.0");

  private final String name;

  SemanticVersion(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
