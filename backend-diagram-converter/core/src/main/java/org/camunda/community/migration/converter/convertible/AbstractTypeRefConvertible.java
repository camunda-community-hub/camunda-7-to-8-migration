package org.camunda.community.migration.converter.convertible;

public abstract class AbstractTypeRefConvertible extends AbstractDmnConvertible {
  private String typeRef;

  public String getTypeRef() {
    return typeRef;
  }

  public void setTypeRef(String typeRef) {
    this.typeRef = typeRef;
  }
}
