package org.camunda.community.migration.converter.visitor;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;

public abstract class AbstractDelegateImplementationVisitor
    extends AbstractSupportedAttributeVisitor {
  private static final Set<String> IGNORE =
      Stream.of("taskListener", "executionListener", "errorEventDefinition")
          .collect(Collectors.toSet());

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    if (context.getProperties().getDefaultJobTypeEnabled()) {
      context.addConversion(
          ServiceTaskConvertible.class,
          serviceTaskConversion ->
              serviceTaskConversion.addZeebeTaskHeader(attributeLocalName(), attribute));
      context.addConversion(
          ServiceTaskConvertible.class,
          serviceTaskConversion ->
              serviceTaskConversion
                  .getZeebeTaskDefinition()
                  .setType(context.getProperties().getDefaultJobType()));
      return MessageFactory.delegateImplementation(
          attributeLocalName(),
          context.getElement().getLocalName(),
          attribute,
          context.getProperties().getDefaultJobType());
    } else {
      return MessageFactory.delegateImplementationNoDefaultJobType(attributeLocalName(), attribute);
    }
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && !IGNORE.contains(context.getElement().getLocalName());
  }
}
