package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.SubProcessConvertible;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class SubProcessVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "subProcess";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new SubProcessConvertible();
  }
}
