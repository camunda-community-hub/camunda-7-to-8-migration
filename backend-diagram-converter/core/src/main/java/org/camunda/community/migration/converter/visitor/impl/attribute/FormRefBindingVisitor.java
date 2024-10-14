package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible;
import org.camunda.community.migration.converter.convertible.UserTaskConvertible.ZeebeFormDefinition.ZeebeFormDefinitionBindingType;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class FormRefBindingVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "formRefBinding";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    if (SemanticVersion.parse(context.getProperties().getPlatformVersion()).ordinal()
            >= SemanticVersion._8_6.ordinal()
        && !attribute.equals("version")) {
      context.addConversion(
          UserTaskConvertible.class,
          userTask -> userTask.getZeebeFormDefinition().setBindingType(mapBindingType(attribute)));
      return MessageFactory.formRefBinding();
    } else {

      return MessageFactory.attributeNotSupported(
          attributeLocalName(), context.getElement().getLocalName(), attribute);
    }
  }

  private ZeebeFormDefinitionBindingType mapBindingType(String attribute) {
    if (attribute.equals("deployment")) {
      return ZeebeFormDefinitionBindingType.deployment;
    }
    return null;
  }
}
