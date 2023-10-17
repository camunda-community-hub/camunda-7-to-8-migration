package org.camunda.community.migration.converter.visitor.impl.eventReference;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.MessageConvertible;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventReferenceVisitor;

public class MessageVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "message";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new MessageConvertible();
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    context.addMessage(MessageFactory.correlationKeyHint());
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }
}
