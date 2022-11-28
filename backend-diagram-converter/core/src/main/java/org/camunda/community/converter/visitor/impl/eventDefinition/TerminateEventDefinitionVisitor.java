package org.camunda.community.converter.visitor.impl.eventDefinition;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractEventDefinitionVisitor;

public class TerminateEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "terminateEventDefinition";
  }

  // TODO this is supported in 8.1
  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return false;
  }
}
