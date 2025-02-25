package org.camunda.community.migration.converter.convertible;

public class ProcessConvertible extends AbstractExecutionListenerConvertible
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
