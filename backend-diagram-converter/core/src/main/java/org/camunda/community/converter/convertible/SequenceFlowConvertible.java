package org.camunda.community.converter.convertible;

public class SequenceFlowConvertible implements Convertible {
  private String conditionExpression;

  public String getConditionExpression() {
    return conditionExpression;
  }

  public void setConditionExpression(String conditionExpression) {
    this.conditionExpression = conditionExpression;
  }
}
