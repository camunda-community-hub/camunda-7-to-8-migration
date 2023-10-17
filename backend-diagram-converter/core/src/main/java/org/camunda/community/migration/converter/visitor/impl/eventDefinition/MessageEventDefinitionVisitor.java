package org.camunda.community.migration.converter.visitor.impl.eventDefinition;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventDefinitionVisitor;

public class MessageEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "messageEventDefinition";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }
}
