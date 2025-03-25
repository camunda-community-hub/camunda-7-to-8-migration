package org.camunda.community.migration.converter.visitor.impl.eventDefinition;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventDefinitionVisitor;

public class EscalationEventDefinitionVisitor extends AbstractEventDefinitionVisitor {
  @Override
  public String localName() {
    return "escalationEventDefinition";
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    if (isThrowing(context)
        || isAttachedToSubprocess(context)
        || isAttachedToCallActivity(context)) {
      return SemanticVersion._8_2;
    }
    return null;
  }

  private boolean isThrowing(DomElementVisitorContext context) {
    return !isBoundaryEvent(context.getElement());
  }

  private boolean isAttachedToCallActivity(DomElementVisitorContext context) {
    return isBoundaryEvent(context.getElement())
        && findAttachedActivity(context.getElement()).getLocalName().equals("callActivity");
  }

  private boolean isAttachedToSubprocess(DomElementVisitorContext context) {
    return isBoundaryEvent(context.getElement())
        && findAttachedActivity(context.getElement()).getLocalName().equals("subProcess");
  }
}
