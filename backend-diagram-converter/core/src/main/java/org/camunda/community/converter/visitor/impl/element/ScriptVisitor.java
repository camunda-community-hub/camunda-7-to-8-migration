package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class ScriptVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "script";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Scripts can only be written in FEEL in Zeebe. Please review";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
