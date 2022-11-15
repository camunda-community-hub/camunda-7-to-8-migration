package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class ScriptTaskVisitor extends AbstractActivityVisitor {
  private static final String SCRIPT_FORMAT_HEADER_NAME = "scriptFormat";

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
    String scriptFormat = context.getElement().getAttribute(SCRIPT_FORMAT_HEADER_NAME);
    if (scriptFormat != null && scriptFormat.trim().length() > 0) {
      context.addConversion(
          ServiceTaskConvertible.class,
          convertible -> convertible.addZeebeTaskHeader(SCRIPT_FORMAT_HEADER_NAME, scriptFormat));
      context.addMessage(
          Severity.TASK, MessageFactory.scriptFormat(SCRIPT_FORMAT_HEADER_NAME, scriptFormat));
    } else {
      context.addMessage(Severity.TASK, MessageFactory.scriptFormatMissing());
    }
  }
}
