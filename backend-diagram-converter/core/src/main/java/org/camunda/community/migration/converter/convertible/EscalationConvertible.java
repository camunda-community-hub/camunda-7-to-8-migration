package org.camunda.community.migration.converter.convertible;

public class EscalationConvertible implements Convertible {
  private String escalationCode;

  public String getEscalationCode() {
    return escalationCode;
  }

  public void setEscalationCode(String escalationCode) {
    this.escalationCode = escalationCode;
  }
}
