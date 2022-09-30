package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.UserTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class UserTaskFormKeyVisitor extends AbstractSupportedAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "formKey";
  }

  @Override
  protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    context.addConversion(
        UserTaskConvertible.class,
        userTaskConversion -> userTaskConversion.getZeebeFormDefinition().setFormKey(attribute));
    return "formKey is now set according to zeebe namespace. Please review your form binding";
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && context.getElement().getLocalName().equals("userTask");
  }
}
