package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.convertible.UserTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class UserTaskFormRefVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "formRef";
  }

  @Override
  protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    String formRef = ExpressionUtil.transform(attribute, false).orElse(attribute);
    context.addConversion(
        UserTaskConvertible.class,
        conversion -> conversion.getZeebeFormDefinition().setFormKey(formRef));
    return "Was mapped to formKey";
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && context.getElement().getLocalName().equals("userTask");
  }
}
