package org.camunda.community.converter.convertible;

public class BusinessRuleTaskConvertible extends AbstractActivityConvertible {
  private final ZeebeCalledDecision zeebeCalledDecision = new ZeebeCalledDecision();

  public ZeebeCalledDecision getZeebeCalledDecision() {
    return zeebeCalledDecision;
  }

  public static class ZeebeCalledDecision {
    private String decisionId;
    private String resultVariable;

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
  }
}
