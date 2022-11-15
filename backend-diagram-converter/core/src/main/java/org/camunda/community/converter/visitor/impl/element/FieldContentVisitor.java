package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public abstract class FieldContentVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.fieldContent(context.getElement().getLocalName());
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }

  public static class FieldVisitor extends FieldContentVisitor {
    @Override
    public String localName() {
      return "field";
    }
  }

  public static class ExpressionVisitor extends FieldContentVisitor {
    @Override
    public String localName() {
      return "expression";
    }
  }

  public static class StringVisitor extends FieldContentVisitor {
    @Override
    public String localName() {
      return "string";
    }
  }
}
