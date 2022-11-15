package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.camunda.community.converter.expression.ExpressionTransformer;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class DecisionRefVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "decisionRef";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(attribute);
    context.addConversion(
        BusinessRuleTaskConvertible.class,
        conversion ->
            conversion
                .getZeebeCalledDecision()
                .setDecisionId(transformationResult.getNewExpression()));
    return MessageFactory.decisionRef(
        attributeLocalName(), context.getElement().getLocalName(), transformationResult);
  }
}
