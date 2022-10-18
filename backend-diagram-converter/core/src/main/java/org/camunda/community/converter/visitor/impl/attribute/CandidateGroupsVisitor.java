package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.convertible.UserTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class CandidateGroupsVisitor extends AbstractSupportedAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "candidateGroups";
  }

  @Override
  protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    String candidateGroups = ExpressionUtil.transform(attribute, false).orElse(attribute);
    context.addConversion(
        UserTaskConvertible.class,
        userTaskConversion ->
            userTaskConversion.getZeebeAssignmentDefinition().setCandidateGroups(candidateGroups));
    return "Please review candidate groups '" + attribute + "'";
  }
}
