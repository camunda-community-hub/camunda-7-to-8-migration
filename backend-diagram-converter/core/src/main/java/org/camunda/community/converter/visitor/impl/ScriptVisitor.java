package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractBpmnElementVisitor;

public class ScriptVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  public String localName() {
    return "script";
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {
    String script = context.getElement().getTextContent();
    context.addConversion(
        AbstractDataMapperConvertible.class,
        convertible ->
            convertible.addZeebeTaskHeader(context.getProperties().getScriptHeader(), script));
    context.addMessage(Severity.TASK, MessageFactory.script());
  }
}
