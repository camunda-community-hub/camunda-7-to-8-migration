package org.camunda.community.migration.converter.visitor;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;

public abstract class AbstractDelegateImplementationVisitor
    extends AbstractSupportedAttributeVisitor {
  public static final Pattern DELEGATE_NAME_EXTRACT = Pattern.compile("[#$]\\{(.*)}");
  private static final Set<String> IGNORE =
      Stream.of("taskListener", "executionListener", "errorEventDefinition")
          .collect(Collectors.toSet());

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    if (context.getProperties().getDefaultJobTypeEnabled()) {
      if (context.getProperties().getUseDelegateExpressionAsJobType()) {
        String jobType = extractJobType(attribute);
        if (jobType != null) {
          context.addConversion(
              ServiceTaskConvertible.class,
              serviceTaskConversion ->
                  serviceTaskConversion.getZeebeTaskDefinition().setType(jobType));
          return MessageFactory.delegateExpressionAsJobType(jobType);
        } else {
          return MessageFactory.delegateExpressionAsJobTypeNull(attribute);
        }
      } else {
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
      }
    } else {
      return MessageFactory.delegateImplementationNoDefaultJobType(attributeLocalName(), attribute);
    }
  }

  private String extractJobType(String attribute) {
    Matcher matcher = DELEGATE_NAME_EXTRACT.matcher(attribute);
    return matcher.find() ? matcher.group(1) : null;
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return super.canVisit(context) && !IGNORE.contains(context.getElement().getLocalName());
  }
}
