package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public abstract class ResultVariableVisitor extends AbstractSupportedAttributeVisitor {
  private static final String HEADER_NAME = "resultVariable";

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
    protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
      context.addConversion(
          BusinessRuleTaskConvertible.class,
          conversion -> conversion.getZeebeCalledDecision().setResultVariable(attribute));
      return MessageFactory.resultVariableBusinessRule(
          attributeLocalName(), context.getElement().getLocalName());
    }

    @Override
    protected boolean canVisitResultVariable(DomElementVisitorContext context) {
      return context.getElement().getLocalName().equals("businessRuleTask");
    }
  }

  public static class ResultVariableOnRestVisitor extends ResultVariableVisitor {

    @Override
    protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
      context.addConversion(
          AbstractDataMapperConvertible.class,
          convertible -> convertible.addZeebeTaskHeader(HEADER_NAME, attribute));
      return MessageFactory.resultVariableRest(
          attributeLocalName(), context.getElement().getLocalName(), HEADER_NAME);
    }

    @Override
    protected boolean canVisitResultVariable(DomElementVisitorContext context) {
      return !context.getElement().getLocalName().equals("businessRuleTask");
    }
  }
}
