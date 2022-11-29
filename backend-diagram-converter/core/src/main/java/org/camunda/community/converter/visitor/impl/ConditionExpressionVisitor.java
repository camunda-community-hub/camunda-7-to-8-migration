package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.camunda.community.converter.expression.ExpressionTransformer;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractBpmnElementVisitor;

public class ConditionExpressionVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  public String localName() {
    return "conditionExpression";
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {

    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(context.getElement().getTextContent());
    context.addConversion(
        SequenceFlowConvertible.class,
        conversion -> conversion.setConditionExpression(transformationResult.getNewExpression()));
    context.addMessage(Severity.TASK, MessageFactory.conditionExpression(transformationResult));
  }
}
