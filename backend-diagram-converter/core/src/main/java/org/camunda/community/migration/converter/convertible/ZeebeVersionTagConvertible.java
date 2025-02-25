package org.camunda.community.migration.converter.convertible;

public interface ZeebeVersionTagConvertible extends Convertible {
  void setZeebeVersionTag(String zeebeVersionTag);

  String getZeebeVersionTag();
}
