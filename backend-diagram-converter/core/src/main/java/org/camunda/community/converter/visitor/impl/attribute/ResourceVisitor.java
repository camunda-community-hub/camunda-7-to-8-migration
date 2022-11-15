package org.camunda.community.converter.visitor.impl.attribute;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractSupportedAttributeVisitor;

public class ResourceVisitor extends AbstractSupportedAttributeVisitor {
  private static final String HEADER_NAME = "resource";

  @Override
  public String attributeLocalName() {
    return "resource";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    context.addConversion(
        AbstractDataMapperConvertible.class,
        convertible -> convertible.addZeebeTaskHeader(HEADER_NAME, attribute));
    return MessageFactory.resource(
        attributeLocalName(), context.getElement().getLocalName(), HEADER_NAME);
  }
}
