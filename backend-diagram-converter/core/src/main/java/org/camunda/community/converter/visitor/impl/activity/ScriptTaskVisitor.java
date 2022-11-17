package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class ScriptTaskVisitor extends AbstractActivityVisitor {

  @Override
  public String localName() {
    return "scriptTask";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ServiceTaskConvertible();
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    String scriptFormat =
        context
            .getElement()
            .getAttribute(context.getProperties().getBpmnNamespace().getUri(), "scriptFormat");
    if (scriptFormat != null && scriptFormat.trim().length() > 0) {
      context.addConversion(
          ServiceTaskConvertible.class,
          convertible ->
              convertible.addZeebeTaskHeader(
                  context.getProperties().getScriptFormatHeader().getName(), scriptFormat));
      context.addConversion(
          ServiceTaskConvertible.class,
          convertible ->
              convertible
                  .getZeebeTaskDefinition()
                  .setType(context.getProperties().getScriptJobType().getType()));
      context.addMessage(
          Severity.TASK,
          MessageFactory.scriptFormat(
              context.getProperties().getScriptFormatHeader().getName(), scriptFormat));
      context.addMessage(
          Severity.TASK,
          MessageFactory.scriptJobType(
              context.getElement().getLocalName(),
              context.getProperties().getScriptJobType().getType()));
    } else {
      context.addMessage(Severity.TASK, MessageFactory.scriptFormatMissing());
    }
  }
}
