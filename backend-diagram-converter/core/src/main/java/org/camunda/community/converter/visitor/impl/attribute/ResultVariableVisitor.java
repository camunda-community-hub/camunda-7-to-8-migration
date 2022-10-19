package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public abstract class ResultVariableVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "resultVariable";
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && canVisitResultVariable(context);
  }

  protected abstract boolean canVisitResultVariable(DomElementVisitorContext context);

  public static class ResultVariableOnBusinessRuleTaskVisitor extends ResultVariableVisitor {
    @Override
    protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
      context.addConversion(
          BusinessRuleTaskConvertible.class,
          conversion -> conversion.getZeebeCalledDecision().setResultVariable(attribute));
      return "Result variable is a simple string";
    }

    @Override
    protected boolean canVisitResultVariable(DomElementVisitorContext context) {
      return context.getElement().getLocalName().equals("businessRuleTask");
    }
  }

  public static class ResultVariableOnRestVisitor extends ResultVariableVisitor {

    @Override
    protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
      context.addConversion(
          AbstractDataMapperConvertible.class,
          convertible -> convertible.addZeebeTaskHeader("resultVariable", attribute));
      return "Result variable was mapped as header 'resultVariable'";
    }

    @Override
    protected boolean canVisitResultVariable(DomElementVisitorContext context) {
      return !context.getElement().getLocalName().equals("businessRuleTask");
    }
  }
}
