package org.camunda.community.migration.converter.convertible;

public class ErrorConvertible implements Convertible {
  private String errorCode;

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }
}
