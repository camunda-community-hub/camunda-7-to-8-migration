package org.camunda.community.migration.converter.visitor.impl;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;

public class CompletionConditionVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {
    String textContent = context.getElement().getTextContent();
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(textContent);
    context.addConversion(
        AbstractActivityConvertible.class,
        conversion ->
            conversion
                .getBpmnMultiInstanceLoopCharacteristics()
                .setCompletionCondition(transformationResult.getFeelExpression()));
    Message message;
    if (transformationResult.hasExecution()) {
      message =
          MessageFactory.completionConditionExecution(
              transformationResult.getJuelExpression(), transformationResult.getFeelExpression());
    } else if (transformationResult.hasMethodInvocation()) {
      message =
          MessageFactory.completionConditionMethod(
              transformationResult.getJuelExpression(), transformationResult.getFeelExpression());
    } else {
      message =
          MessageFactory.completionCondition(
              transformationResult.getJuelExpression(), transformationResult.getFeelExpression());
    }
    context.addMessage(message);
  }

  @Override
  public String localName() {
    return "completionCondition";
  }
}
