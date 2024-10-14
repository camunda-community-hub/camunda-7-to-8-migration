package org.camunda.community.migration.converter.convertible;

public class ProcessConvertible extends AbstractExecutionListenerConvertible {
  private String zeebeVersionTag;

  public String getZeebeVersionTag() {
    return zeebeVersionTag;
  }

  public void setZeebeVersionTag(String zeebeVersionTag) {
    this.zeebeVersionTag = zeebeVersionTag;
  }
}
