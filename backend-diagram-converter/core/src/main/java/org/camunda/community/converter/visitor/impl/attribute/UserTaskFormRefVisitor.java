package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.UserTaskConvertible;
import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.camunda.community.converter.expression.ExpressionTransformer;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class UserTaskFormRefVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "formRef";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(attribute);
    context.addConversion(
        UserTaskConvertible.class,
        conversion ->
            conversion
                .getZeebeFormDefinition()
                .setFormKey(transformationResult.getNewExpression()));
    return MessageFactory.formRef(
        attributeLocalName(), context.getElement().getLocalName(), transformationResult);
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    // to prevent formRef finding on start event
    return super.canVisit(context) && context.getElement().getLocalName().equals("userTask");
  }
}
