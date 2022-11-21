package org.camunda.community.converter.visitor;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;

public abstract class AbstractDelegateImplementationVisitor
    extends AbstractSupportedAttributeVisitor {
  private static final Set<String> IGNORE =
      Stream.of("taskListener", "executionListener", "errorEventDefinition")
          .collect(Collectors.toSet());

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    context.addConversion(
        ServiceTaskConvertible.class,
        serviceTaskConversion ->
            serviceTaskConversion.addZeebeTaskHeader(attributeLocalName(), attribute));
    context.addConversion(
        ServiceTaskConvertible.class,
        serviceTaskConversion ->
            serviceTaskConversion
                .getZeebeTaskDefinition()
                .setType(context.getProperties().getAdapterJobType()));
    return MessageFactory.delegateImplementation(
        attributeLocalName(),
        context.getElement().getLocalName(),
        attribute,
        context.getProperties().getAdapterJobType());
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && !IGNORE.contains(context.getElement().getLocalName());
  }
}
