package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.migration.converter.convertible.ScriptTaskConvertible;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;
import org.camunda.community.migration.converter.visitor.impl.activity.ScriptTaskVisitor;

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
    if (ScriptTaskVisitor.canBeInternalScript(context)) {
      context.addConversion(
          ScriptTaskConvertible.class, convertible -> convertible.setExpression("=" + script));
      context.addMessage(MessageFactory.internalScript());
    } else {
      context.addConversion(
          AbstractDataMapperConvertible.class,
          convertible ->
              convertible.addZeebeTaskHeader(context.getProperties().getScriptHeader(), script));
      context.addMessage(MessageFactory.script());
    }
    context.addElementToRemove();
  }
}
