package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.AbstractFlownodeConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class PropertyVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "property";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    String name = context.getElement().getAttribute("name");
    String value = context.getElement().getAttribute("value");
    context.addConversion(
        AbstractFlownodeConvertible.class, conversion -> conversion.addZeebeProperty(name, value));
    return MessageFactory.property(context.getElement().getLocalName(), name);
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }
}
