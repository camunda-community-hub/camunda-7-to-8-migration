package org.camunda.community.migration.converter.convertible;

public class CallActivityConvertible extends AbstractActivityConvertible {
  private final ZeebeCalledElement zeebeCalledElement = new ZeebeCalledElement();

  public ZeebeCalledElement getZeebeCalledElement() {
    return zeebeCalledElement;
  }

  public static class ZeebeCalledElement {
    private String processId;
    private boolean propagateAllChildVariables = false;

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
  }
}
