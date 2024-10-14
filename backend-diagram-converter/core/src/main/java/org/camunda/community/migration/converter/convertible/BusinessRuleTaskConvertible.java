package org.camunda.community.migration.converter.convertible;

public class BusinessRuleTaskConvertible extends AbstractActivityConvertible {
  private final ZeebeCalledDecision zeebeCalledDecision = new ZeebeCalledDecision();

  public ZeebeCalledDecision getZeebeCalledDecision() {
    return zeebeCalledDecision;
  }

  public static class ZeebeCalledDecision {
    private String decisionId;
    private String resultVariable;
    private ZeebeCalledDecisionBindingType bindingType;
    private String versionTag;

    public String getDecisionId() {
      return decisionId;
    }

    public void setDecisionId(String decisionId) {
      this.decisionId = decisionId;
    }

    public String getResultVariable() {
      return resultVariable;
    }

    public void setResultVariable(String resultVariable) {
      this.resultVariable = resultVariable;
    }

    public ZeebeCalledDecisionBindingType getBindingType() {
      return bindingType;
    }

    public void setBindingType(ZeebeCalledDecisionBindingType bindingType) {
      this.bindingType = bindingType;
    }

    public String getVersionTag() {
      return versionTag;
    }

    public void setVersionTag(String versionTag) {
      this.versionTag = versionTag;
    }

    public enum ZeebeCalledDecisionBindingType {
      versionTag,
      deployment
    }
  }
}
