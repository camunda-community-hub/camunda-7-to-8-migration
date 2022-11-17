package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.camunda.community.converter.expression.ExpressionTransformer;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class ConditionExpressionVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri(DomElementVisitorContext context) {
    return context.getProperties().getBpmnNamespace().getUri();
  }

  @Override
  public String localName() {
    return "conditionExpression";
  }

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {

    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(context.getElement().getTextContent());
    context.addConversion(
        SequenceFlowConvertible.class,
        conversion -> conversion.setConditionExpression(transformationResult.getNewExpression()));
    context.addMessage(Severity.TASK, MessageFactory.conditionExpression(transformationResult));
  }
}
