package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class FollowUpDateVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "followUpDate";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult followUpDate = ExpressionTransformer.transform(attribute);
    context.addConversion(
        UserTaskConvertible.class,
        conv -> conv.getZeebeTaskSchedule().setFollowUpDate(followUpDate.getFeelExpression()));
    return MessageFactory.followUpDate(followUpDate);
  }
}
