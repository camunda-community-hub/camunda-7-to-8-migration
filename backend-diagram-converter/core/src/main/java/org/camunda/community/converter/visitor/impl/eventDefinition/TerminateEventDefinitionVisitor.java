package org.camunda.community.converter.visitor.impl.eventDefinition;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.version.VersionComparison;
import org.camunda.community.converter.visitor.AbstractEventDefinitionVisitor;

public class TerminateEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "terminateEventDefinition";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return VersionComparison.isSupported(
        context.getProperties().getPlatformVersion(), SemanticVersion._8_1_0.toString());
  }
}
