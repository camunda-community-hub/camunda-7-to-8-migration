package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class DecisionRefVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "decisionRef";
  }

  @Override
  protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    String decisionId = ExpressionUtil.transform(attribute, false).orElse(attribute);
    context.addConversion(
        BusinessRuleTaskConvertible.class,
        conversion -> conversion.getZeebeCalledDecision().setDecisionId(decisionId));
    return "Decision ref was transferred: '" + decisionId + "'. Please review possible expression";
  }
}
