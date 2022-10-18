package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.converter.visitor.AbstractProcessElementVisitor;

public class SequenceFlowVisitor extends AbstractProcessElementVisitor {
  @Override
  public String localName() {
    return "sequenceFlow";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new SequenceFlowConvertible();
  }
}
