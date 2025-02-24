package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResultMessageFactory;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class DueDateVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "dueDate";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult dueDate = ExpressionTransformer.transform("Due date", attribute);
    context.addConversion(
        UserTaskConvertible.class,
        conv -> conv.getZeebeTaskSchedule().setDueDate(dueDate.getFeelExpression()));
    return ExpressionTransformationResultMessageFactory.getMessage(
        dueDate, "https://docs.camunda.io/docs/components/modeler/bpmn/user-tasks/#scheduling");
  }
}
