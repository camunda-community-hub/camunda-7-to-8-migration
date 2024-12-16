package org.camunda.community.migration.converter.visitor.impl.element;

import static org.camunda.community.migration.converter.visitor.AbstractDelegateImplementationVisitor.*;

import java.util.regex.Matcher;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractExecutionListenerConvertible;
import org.camunda.community.migration.converter.convertible.AbstractExecutionListenerConvertible.ZeebeExecutionListener;
import org.camunda.community.migration.converter.convertible.AbstractExecutionListenerConvertible.ZeebeExecutionListener.EventType;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor.ListenerImplementation.DelegateExpressionImplementation;

public class ExecutionListenerVisitor extends AbstractListenerVisitor {
  @Override
  public String localName() {
    return "executionListener";
  }

  @Override
  protected Message visitListener(
      DomElementVisitorContext context, String event, ListenerImplementation implementation) {
    if (isExecutionListenerSupported(
        SemanticVersion.parse(context.getProperties().getPlatformVersion()), event)) {
      ZeebeExecutionListener executionListener = new ZeebeExecutionListener();
      executionListener.setEventType(EventType.valueOf(event));
      if (implementation instanceof DelegateExpressionImplementation) {
        Matcher matcher = DELEGATE_NAME_EXTRACT.matcher(implementation.implementation());
        String delegateName = matcher.find() ? matcher.group(1) : implementation.implementation();
        executionListener.setListenerType(delegateName);
      } else {
        executionListener.setListenerType(implementation.implementation());
      }
      context.addConversion(
          AbstractExecutionListenerConvertible.class,
          c -> c.addZeebeExecutionListener(executionListener));
      return MessageFactory.executionListenerSupported(event, implementation.implementation());
    }
    return MessageFactory.executionListener(
        event, ListenerImplementation.type(implementation), implementation.implementation());
  }

  private boolean isExecutionListenerSupported(SemanticVersion version, String event) {
    return version.ordinal() >= SemanticVersion._8_6.ordinal() && isKnownEventType(event);
  }

  private boolean isKnownEventType(String event) {
    for (EventType eventType : EventType.values()) {
      if (eventType.name().equals(event)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return isExecutionListenerSupported(
        SemanticVersion.parse(context.getProperties().getPlatformVersion()),
        findEventName(context));
  }
}
