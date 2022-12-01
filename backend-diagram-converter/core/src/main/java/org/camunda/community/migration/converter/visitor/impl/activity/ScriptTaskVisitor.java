package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class ScriptTaskVisitor extends AbstractActivityVisitor {

  @Override
  public String localName() {
    return "scriptTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ServiceTaskConvertible();
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    String scriptFormat = context.getElement().getAttribute(NamespaceUri.BPMN, "scriptFormat");
    if (scriptFormat != null && scriptFormat.trim().length() > 0) {
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
    } else {
      context.addMessage(MessageFactory.scriptFormatMissing());
    }
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }
}
