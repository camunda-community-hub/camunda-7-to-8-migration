package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.CallActivityConvertible;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.camunda.community.converter.expression.ExpressionTransformer;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class CallActivityVisitor extends AbstractActivityVisitor {

  public static final String CALLED_ELEMENT = "calledElement";

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

    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(
            context.getElement().getAttribute(NamespaceUri.BPMN, CALLED_ELEMENT));
    if (transformationResult == null) {
      context.addMessage(Severity.WARNING, MessageFactory.callActivityNoCalledElementHint());
    } else {
      context.addConversion(
          CallActivityConvertible.class,
          conversion ->
              conversion
                  .getZeebeCalledElement()
                  .setProcessId(transformationResult.getNewExpression()));
      context.addMessage(
          Severity.TASK,
          MessageFactory.calledElement(CALLED_ELEMENT, localName(), transformationResult));
    }
  }
}
