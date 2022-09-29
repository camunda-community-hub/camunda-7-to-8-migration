package org.camunda.community.converter.visitor.impl.eventReference;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.MessageConvertible;
import org.camunda.community.converter.visitor.AbstractEventReferenceVisitor;

public class MessageVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "message";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new MessageConvertible();
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    context.addMessage(Severity.TASK, "Please define a correlation key");
  }
}
