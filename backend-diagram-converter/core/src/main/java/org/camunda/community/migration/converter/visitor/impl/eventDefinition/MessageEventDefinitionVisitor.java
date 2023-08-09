package org.camunda.community.migration.converter.visitor.impl.eventDefinition;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventDefinitionVisitor;

public class MessageEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "messageEventDefinition";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  protected void visitEventDefinition(DomElementVisitorContext context) {
    if (!isStartEvent(context.getElement())) {
      context.addMessage(MessageFactory.correlationKeyHint());
    }
  }
}
