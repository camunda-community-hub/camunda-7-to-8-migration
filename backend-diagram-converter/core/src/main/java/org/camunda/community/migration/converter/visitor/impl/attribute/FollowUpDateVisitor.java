package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResultMessageFactory;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class FollowUpDateVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "followUpDate";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult followUpDate =
        ExpressionTransformer.transform("Follow up date", attribute);
    context.addConversion(
        UserTaskConvertible.class,
        conv -> conv.getZeebeTaskSchedule().setFollowUpDate(followUpDate.getFeelExpression()));
    return ExpressionTransformationResultMessageFactory.getMessage(
        followUpDate,
        "https://docs.camunda.io/docs/components/modeler/bpmn/user-tasks/#scheduling");
  }
}
