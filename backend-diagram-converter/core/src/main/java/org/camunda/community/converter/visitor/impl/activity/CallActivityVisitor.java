package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.convertible.CallActivityConvertible;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class CallActivityVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "callActivity";
  }

  @Override
  public boolean canBeConverted(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new CallActivityConvertible();
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    String calledElement =
        ExpressionUtil.transform(context.getElement().getAttribute("calledElement"), false)
            .orElse(context.getElement().getAttribute("calledElement"));
    if (calledElement == null) {
      context.addMessage(
          Severity.WARNING,
          "There has to be a calledElement present on the call activity. Please keep in mind that CMMN is no supported with Zeebe");
    }
    context.addConversion(
        CallActivityConvertible.class,
        conversion -> conversion.getZeebeCalledElement().setProcessId(calledElement));
    context.addMessage(Severity.TASK, "Please review called element '" + calledElement + "'");
  }
}
