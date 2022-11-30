package org.camunda.community.migration.converter.visitor.impl.eventDefinition;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventDefinitionVisitor;

public class TerminateEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "terminateEventDefinition";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_1_0;
  }
}
