package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class CandidateGroupsVisitor extends AbstractSupportedAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "candidateGroups";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult candidateGroups = ExpressionTransformer.transform(attribute);
    context.addConversion(
        UserTaskConvertible.class,
        userTaskConversion ->
            userTaskConversion
                .getZeebeAssignmentDefinition()
                .setCandidateGroups(candidateGroups.getNewExpression()));
    return MessageFactory.candidateGroups(
        attributeLocalName(), context.getElement().getLocalName(), candidateGroups);
  }
}
