package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class CallActivityVisitor extends AbstractActivityVisitor {

  public static final String CALLED_ELEMENT = "calledElement";

  @Override
  public String localName() {
    return "callActivity";
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
    if (transformationResult != null) {
      context.addConversion(
          CallActivityConvertible.class,
          conversion ->
              conversion
                  .getZeebeCalledElement()
                  .setProcessId(transformationResult.getFeelExpression()));
      context.addMessage(
          MessageFactory.calledElement(
              CALLED_ELEMENT,
              localName(),
              transformationResult.getJuelExpression(),
              transformationResult.getFeelExpression()));
    }
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0;
  }
}
