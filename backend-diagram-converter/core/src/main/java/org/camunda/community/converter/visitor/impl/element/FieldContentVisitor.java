package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public abstract class FieldContentVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    return "Field injection does not work any more. Please review your implementation";
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
