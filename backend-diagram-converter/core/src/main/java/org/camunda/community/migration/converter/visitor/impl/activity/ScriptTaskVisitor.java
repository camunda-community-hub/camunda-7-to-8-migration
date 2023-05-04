package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ScriptTaskConvertible;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class ScriptTaskVisitor extends AbstractActivityVisitor {

  public static boolean canBeInternalScript(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    while (!element.getLocalName().equals("scriptTask")
        && !element.getRootElement().equals(element)) {
      element = element.getParentElement();
    }
    String scriptFormat = element.getAttribute(NamespaceUri.BPMN, "scriptFormat");
    return scriptFormat != null
        && scriptFormat.trim().length() > 0
        && scriptFormat.equalsIgnoreCase("feel")
        && SemanticVersion._8_2_0.ordinal()
            <= SemanticVersion.parse(context.getProperties().getPlatformVersion()).ordinal();
  }

  @Override
  public String localName() {
    return "scriptTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    if (canBeInternalScript(context)) {
      return new ScriptTaskConvertible();
    }
    return new ServiceTaskConvertible();
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    String scriptFormat = context.getElement().getAttribute(NamespaceUri.BPMN, "scriptFormat");
    if (scriptFormat != null && scriptFormat.trim().length() > 0) {
      if (!canBeInternalScript(context)) {
        context.addConversion(
            ServiceTaskConvertible.class,
            convertible ->
                convertible.addZeebeTaskHeader(
                    context.getProperties().getScriptFormatHeader(), scriptFormat));
        context.addConversion(
            ServiceTaskConvertible.class,
            convertible ->
                convertible
                    .getZeebeTaskDefinition()
                    .setType(context.getProperties().getScriptJobType()));
        context.addMessage(
            MessageFactory.scriptFormat(
                context.getProperties().getScriptFormatHeader(), scriptFormat));
        context.addMessage(
            MessageFactory.scriptJobType(
                context.getElement().getLocalName(), context.getProperties().getScriptJobType()));
      }
    }
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
