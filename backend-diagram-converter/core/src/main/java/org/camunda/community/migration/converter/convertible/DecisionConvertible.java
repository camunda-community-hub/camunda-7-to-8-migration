package org.camunda.community.migration.converter.convertible;

public class DecisionConvertible extends AbstractDmnConvertible
    implements ZeebeVersionTagConvertible {
  private String zeebeVersionTag;

  @Override
  public String getZeebeVersionTag() {
    return zeebeVersionTag;
  }

  @Override
  public void setZeebeVersionTag(String zeebeVersionTag) {
    this.zeebeVersionTag = zeebeVersionTag;
  }
}
