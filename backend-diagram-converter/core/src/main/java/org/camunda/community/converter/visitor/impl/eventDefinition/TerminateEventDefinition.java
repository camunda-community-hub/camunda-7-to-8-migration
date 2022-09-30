package org.camunda.community.converter.visitor.impl.eventDefinition;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractEventDefinitionVisitor;

public class TerminateEventDefinition extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "terminateEventDefinition";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return false;
  }
}
