package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractExecutionListenerConvertible;
import org.camunda.community.migration.converter.convertible.AbstractExecutionListenerConvertible.ZeebeExecutionListener;
import org.camunda.community.migration.converter.convertible.AbstractExecutionListenerConvertible.ZeebeExecutionListener.EventType;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor;

public class ExecutionListenerVisitor extends AbstractListenerVisitor {
  @Override
  public String localName() {
    return "executionListener";
  }

  @Override
  protected Message visitListener(
      DomElementVisitorContext context, String event, String implementation) {
    if (isExecutionListenerSupported(
        SemanticVersion.parse(context.getProperties().getPlatformVersion()))) {
      ZeebeExecutionListener executionListener = new ZeebeExecutionListener();
      executionListener.setEventType(EventType.valueOf(event));
      executionListener.setListenerType(implementation);
      context.addConversion(
          AbstractExecutionListenerConvertible.class,
          c -> c.addZeebeExecutionListener(executionListener));
      return MessageFactory.executionListenerSupported(event, implementation);
    }
    return MessageFactory.executionListener(event, implementation);
  }

  private boolean isExecutionListenerSupported(SemanticVersion version) {
    return version.ordinal() >= SemanticVersion._8_6.ordinal();
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return isExecutionListenerSupported(
        SemanticVersion.parse(context.getProperties().getPlatformVersion()));
  }
}
