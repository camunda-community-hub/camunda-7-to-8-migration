package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class PropertiesVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "properties";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return null;
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected boolean isSilent() {
    return true;
  }
}
