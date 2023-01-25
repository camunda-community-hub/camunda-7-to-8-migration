package org.camunda.community.migration.converter.convertible;

public class ScriptTaskConvertible extends AbstractActivityConvertible {
  private String expression;
  private String resultVariable;

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public String getResultVariable() {
    return resultVariable;
  }

  public void setResultVariable(String resultVariable) {
    this.resultVariable = resultVariable;
  }
}
