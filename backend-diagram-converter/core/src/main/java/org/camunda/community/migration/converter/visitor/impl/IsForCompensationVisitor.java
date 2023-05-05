package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractBpmnAttributeVisitor;

public class IsForCompensationVisitor extends AbstractBpmnAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "isForCompensation";
  }

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    if (Boolean.parseBoolean(attribute)) {
      context.addMessage(
          MessageFactory.elementNotSupportedHint(
              "Compensation Task", context.getProperties().getPlatformVersion()));
    }
  }
}
