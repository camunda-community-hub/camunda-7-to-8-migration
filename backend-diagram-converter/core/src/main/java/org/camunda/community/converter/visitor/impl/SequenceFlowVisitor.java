package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractProcessElementVisitor;

public class SequenceFlowVisitor extends AbstractProcessElementVisitor {
  @Override
  public String localName() {
    return "sequenceFlow";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new SequenceFlowConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
