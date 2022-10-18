package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.AbstractFlownodeConvertible;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class PropertyVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "property";
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    String name = context.getElement().getAttribute("name");
    String value = context.getElement().getAttribute("value");
    context.addConversion(
        AbstractFlownodeConvertible.class, conversion -> conversion.addZeebeProperty(name, value));
    return "Property '" + name + "' lives in Zeebe namespace now";
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }
}
