package org.camunda.community.converter.visitor.impl.eventDefinition;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractEventDefinitionVisitor;

public class MessageEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "messageEventDefinition";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }
}
