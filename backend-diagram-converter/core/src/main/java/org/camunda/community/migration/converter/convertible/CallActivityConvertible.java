package org.camunda.community.migration.converter.convertible;

public class CallActivityConvertible extends AbstractActivityConvertible {
  private final ZeebeCalledElement zeebeCalledElement = new ZeebeCalledElement();

  public ZeebeCalledElement getZeebeCalledElement() {
    return zeebeCalledElement;
  }

  public static class ZeebeCalledElement {
    private String processId;
    private boolean propagateAllChildVariables = false;
    private boolean propagateAllParentVariables = false;
    private ZeebeCalledElementBindingType bindingType;
    private String versionTag;

    public ZeebeCalledElementBindingType getBindingType() {
      return bindingType;
    }

    public void setBindingType(ZeebeCalledElementBindingType bindingType) {
      this.bindingType = bindingType;
    }

    public String getVersionTag() {
      return versionTag;
    }

    public void setVersionTag(String versionTag) {
      this.versionTag = versionTag;
    }

    public boolean isPropagateAllParentVariables() {
      return propagateAllParentVariables;
    }

    public void setPropagateAllParentVariables(boolean propagateAllParentVariables) {
      this.propagateAllParentVariables = propagateAllParentVariables;
    }

    public String getProcessId() {
      return processId;
    }

    public void setProcessId(String processId) {
      this.processId = processId;
    }

    public boolean isPropagateAllChildVariables() {
      return propagateAllChildVariables;
    }

    public void setPropagateAllChildVariables(boolean propagateAllChildVariables) {
      this.propagateAllChildVariables = propagateAllChildVariables;
    }

    public enum ZeebeCalledElementBindingType {
      versionTag,
      deployment
    }
  }
}
