package org.camunda.community.converter.visitor.impl.eventDefinition;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.visitor.AbstractEventDefinitionVisitor;

public class ConditionalEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "conditionalEventDefinition";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return false;
  }
}
