package org.camunda.community.migration.converter.visitor.impl.eventReference;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ErrorConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventReferenceVisitor;

public class ErrorVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "error";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ErrorConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    String errorCode = context.getElement().getAttribute(BPMN, "errorCode");
    ExpressionTransformationResult expressionTransformationResult =
        ExpressionTransformer.transform(errorCode);
    if (expressionTransformationResult.getNewExpression().startsWith("=")) {
      context.addMessage(MessageFactory.errorCodeNoExpression());
    }
    // this can be enabled as soon as error codes can be expressions
    /*
    if (SemanticVersion.parse(context.getProperties().getPlatformVersion()).ordinal()
        >= SemanticVersion._8_2_0.ordinal()) {
      String errorCode = context.getElement().getAttribute(BPMN, "errorCode");
      if (errorCode != null) {
        ExpressionTransformationResult expressionTransformationResult =
            ExpressionTransformer.transform(errorCode);
        if (!expressionTransformationResult
            .getNewExpression()
            .equals(expressionTransformationResult.getOldExpression())) {
          context.addConversion(
              ErrorConvertible.class,
              convertible ->
                  convertible.setErrorCode(expressionTransformationResult.getNewExpression()));
          context.addMessage(
              MessageFactory.errorCode(
                  expressionTransformationResult.getOldExpression(),
                  expressionTransformationResult.getNewExpression()));
        }
      }
    }*/
  }
}
