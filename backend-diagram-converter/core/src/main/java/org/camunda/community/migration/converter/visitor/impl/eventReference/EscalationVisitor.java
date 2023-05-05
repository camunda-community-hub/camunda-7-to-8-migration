package org.camunda.community.migration.converter.visitor.impl.eventReference;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.EscalationConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractEventReferenceVisitor;

public class EscalationVisitor extends AbstractEventReferenceVisitor {
  @Override
  public String localName() {
    return "escalation";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new EscalationConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_2_0;
  }

  @Override
  protected void postCreationVisitor(DomElementVisitorContext context) {
    String escalationCode = context.getElement().getAttribute(BPMN, "escalationCode");
    ExpressionTransformationResult expressionTransformationResult =
        ExpressionTransformer.transform(escalationCode);
    if (expressionTransformationResult.getNewExpression().startsWith("=")) {
      context.addMessage(MessageFactory.escalationCodeNoExpression());
    }
    // this can be enabled as soon as escalation codes can be expressions
    /*
    String escalationCode = context.getElement().getAttribute(NamespaceUri.BPMN, "escalationCode");
    if (escalationCode != null) {

        ExpressionTransformationResult expressionTransformationResult =
            ExpressionTransformer.transform(escalationCode);
        if (!expressionTransformationResult
            .getNewExpression()
            .equals(expressionTransformationResult.getOldExpression())) {
          context.addConversion(
              EscalationConvertible.class,
              convertible ->
                  convertible.setEscalationCode(expressionTransformationResult.getNewExpression()));
          context.addMessage(
              MessageFactory.escalationCode(
                  expressionTransformationResult.getOldExpression(),
                  expressionTransformationResult.getNewExpression()));
      }
    }*/
  }
}
