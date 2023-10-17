package org.camunda.community.migration.converter.visitor.impl.eventDefinition;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventDefinitionVisitor;

public class SignalEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "signalEventDefinition";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    if (isStartEvent(context) && isNotEventSubProcessStartEvent(context.getElement())) {
      return SemanticVersion._8_2;
    }
    return SemanticVersion._8_3;
  }

  private boolean isStartEvent(DomElementVisitorContext context) {
    return context.getElement().getParentElement().getLocalName().equals("startEvent");
  }
}
