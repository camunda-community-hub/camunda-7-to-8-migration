package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.migration.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.migration.converter.convertible.ScriptTaskConvertible;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;
import org.camunda.community.migration.converter.visitor.impl.activity.BusinessRuleTaskVisitor;
import org.camunda.community.migration.converter.visitor.impl.activity.ScriptTaskVisitor;

public abstract class ResultVariableVisitor extends AbstractSupportedAttributeVisitor {

  public static boolean isBusinessRuleTask(DomElementVisitorContext context) {
    return context.getElement().getLocalName().equals("businessRuleTask")
        && BusinessRuleTaskVisitor.isDmnImplementation(context);
  }

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
      return ResultVariableVisitor.isBusinessRuleTask(context);
    }
  }

  public static class ResultVariableOnRestVisitor extends ResultVariableVisitor {

    @Override
    protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
      context.addConversion(
          AbstractDataMapperConvertible.class,
          convertible ->
              convertible.addZeebeTaskHeader(
                  context.getProperties().getResultVariableHeader(), attribute));
      return MessageFactory.resultVariableRest(
          attributeLocalName(),
          context.getElement().getLocalName(),
          context.getProperties().getResultVariableHeader());
    }

    @Override
    protected boolean canVisitResultVariable(DomElementVisitorContext context) {
      return !ResultVariableVisitor.isBusinessRuleTask(context)
          && !ScriptTaskVisitor.canBeInternalScript(context);
    }
  }

  public static class ResultVariableOnInternalScriptVisitor extends ResultVariableVisitor {

    @Override
    protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
      context.addConversion(
          ScriptTaskConvertible.class, convertible -> convertible.setResultVariable(attribute));
      return MessageFactory.resultVariableInternalScript();
    }

    @Override
    protected boolean canVisitResultVariable(DomElementVisitorContext context) {
      return ScriptTaskVisitor.canBeInternalScript(context);
    }
  }
}
