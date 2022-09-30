package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class PotentialStarterVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "potentialStarter";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Potential Starters are currently not managed my Zeebe";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
