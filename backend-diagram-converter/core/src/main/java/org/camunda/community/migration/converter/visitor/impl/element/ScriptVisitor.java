package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public class ScriptVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "script";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    String scriptFormat = context.getElement().getAttribute("scriptFormat");
    String script = detectScript(context);
    return MessageFactory.camundaScript(
        script, scriptFormat, context.getElement().getParentElement().getLocalName());
  }

  private String detectScript(DomElementVisitorContext context) {
    String resource = context.getElement().getAttribute("resource");
    if (resource == null) {
      return context.getElement().getTextContent();

    } else {
      return resource;
    }
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
