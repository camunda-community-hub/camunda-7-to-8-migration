package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class TaskTopicVisitor extends AbstractSupportedAttributeVisitor {

  @Override
  public String attributeLocalName() {
    return "topic";
  }

  @Override
  protected String visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    context.addConversion(
        ServiceTaskConvertible.class,
        serviceTaskConversion -> serviceTaskConversion.getZeebeTaskDefinition().setType(attribute));
    return "Task topic was set as job type";
  }
}
