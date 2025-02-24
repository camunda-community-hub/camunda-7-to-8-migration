package org.camunda.community.migration.converter.expression;

import java.util.Objects;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;

public class ExpressionTransformationResultMessageFactory {
  public static Message getMessage(
      ExpressionTransformationResult transformationResult, String link) {
    // no transformation has happened (because the expression is not an expression)
    if (Objects.equals(
        transformationResult.getFeelExpression(), transformationResult.getJuelExpression())) {
      return MessageFactory.noExpressionTransformation();
    }
    // check for execution reference

    if (transformationResult.getHasExecutionOnly()) {
      return MessageFactory.expressionExecutionNotAvailable(
          transformationResult.getContext(),
          transformationResult.getJuelExpression(),
          transformationResult.getFeelExpression(),
          link);

    } else
    // check for method invocation
    if (transformationResult.getHasMethodInvocation()) {
      return MessageFactory.expressionMethodNotPossible(
          transformationResult.getContext(),
          transformationResult.getJuelExpression(),
          transformationResult.getFeelExpression(),
          link);
    } else {
      // if all is good, just give the default message
      return MessageFactory.expression(
          transformationResult.getContext(),
          transformationResult.getJuelExpression(),
          transformationResult.getFeelExpression(),
          link);
    }
  }
}
