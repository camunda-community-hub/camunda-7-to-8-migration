package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaAttributeVisitor;

public class InputVariableVisitor extends AbstractCamundaAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "inputVariable";
  }

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.addMessage(MessageFactory.inputVariableNotSupported());
  }
}
