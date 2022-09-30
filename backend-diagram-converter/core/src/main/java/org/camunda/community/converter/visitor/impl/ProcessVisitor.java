package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ProcessConvertible;
import org.camunda.community.converter.visitor.AbstractProcessElementVisitor;

public class ProcessVisitor extends AbstractProcessElementVisitor {
  @Override
  public String localName() {
    return "process";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ProcessConvertible();
  }
}
