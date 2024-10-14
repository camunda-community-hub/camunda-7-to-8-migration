package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible.ZeebeCalledElement.ZeebeCalledElementBindingType;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class CalledElementBindingVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "calledElementBinding";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    if (SemanticVersion.parse(context.getProperties().getPlatformVersion()).ordinal()
            >= SemanticVersion._8_6.ordinal()
        && !attribute.equals("version")) {
      context.addConversion(
          CallActivityConvertible.class,
          callActivity ->
              callActivity.getZeebeCalledElement().setBindingType(mapBindingType(attribute)));
      return MessageFactory.calledElementRefBinding();
    } else {

      return MessageFactory.attributeNotSupported(
          attributeLocalName(), context.getElement().getLocalName(), attribute);
    }
  }

  private ZeebeCalledElementBindingType mapBindingType(String attribute) {
    return switch (attribute) {
      case "versionTag" -> ZeebeCalledElementBindingType.versionTag;
      case "deployment" -> ZeebeCalledElementBindingType.deployment;
      default -> null;
    };
  }
}
