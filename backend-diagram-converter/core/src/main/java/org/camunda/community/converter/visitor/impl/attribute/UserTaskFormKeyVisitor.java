package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.UserTaskConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class UserTaskFormKeyVisitor extends AbstractSupportedAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "formKey";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    context.addConversion(
        UserTaskConvertible.class,
        userTaskConversion -> userTaskConversion.getZeebeFormDefinition().setFormKey(attribute));
    return MessageFactory.formKey(attributeLocalName(), context.getElement().getLocalName());
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && context.getElement().getLocalName().equals("userTask");
  }
}
