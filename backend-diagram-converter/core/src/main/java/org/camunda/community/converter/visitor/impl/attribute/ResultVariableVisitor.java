package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class ResultVariableVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "resultVariable";
  }

  @Override
  protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    context.addConversion(
        BusinessRuleTaskConvertible.class,
        conversion -> conversion.getZeebeCalledDecision().setResultVariable(attribute));
    return "Result variable is a simple string";
  }
}
