package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResultMessageFactory;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class CandidateUsersVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "candidateUsers";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult candidateUsers =
        ExpressionTransformer.transform("Candidate users", attribute);
    context.addConversion(
        UserTaskConvertible.class,
        convertible -> convertible.getZeebeAssignmentDefinition().setCandidateUsers(attribute));
    return ExpressionTransformationResultMessageFactory.getMessage(
        candidateUsers,
        "https://docs.camunda.io/docs/components/modeler/bpmn/user-tasks/#assignments");
  }
}
