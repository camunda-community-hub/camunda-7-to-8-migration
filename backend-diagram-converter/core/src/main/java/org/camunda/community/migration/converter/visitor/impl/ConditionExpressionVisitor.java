package org.camunda.community.migration.converter.visitor.impl;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResultMessageFactory;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractBpmnElementVisitor;
import org.camunda.community.migration.converter.visitor.impl.gateway.ComplexGatewayVisitor;
import org.camunda.community.migration.converter.visitor.impl.gateway.ExclusiveGatewayVisitor;
import org.camunda.community.migration.converter.visitor.impl.gateway.InclusiveGatewayVisitor;

public class ConditionExpressionVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return isConditionalFlow(context) ? null : SemanticVersion._8_0;
  }

  private boolean isConditionalFlow(DomElementVisitorContext context) {
    String sourceRef = context.getElement().getParentElement().getAttribute(BPMN, "sourceRef");
    if (sourceRef == null) {
      return false;
    }
    DomElement source = context.getElement().getDocument().getElementById(sourceRef);
    if (source == null) {
      return false;
    }
    return !isGateway(source);
  }

  private boolean isGateway(DomElement element) {
    return element.getNamespaceURI().equals(BPMN)
        && Arrays.asList(
                ExclusiveGatewayVisitor.ELEMENT_LOCAL_NAME,
                InclusiveGatewayVisitor.ELEMENT_LOCAL_NAME,
                ComplexGatewayVisitor.ELEMENT_LOCAL_NAME)
            .contains(element.getLocalName());
  }

  @Override
  public String localName() {
    return "conditionExpression";
  }

  @Override
  protected void visitBpmnElement(DomElementVisitorContext context) {
    String language = context.getElement().getAttribute(BPMN, "language");
    if (StringUtils.isBlank(language)) {
      handleJuelExpression(context);
    } else {
      handleLanguage(context, language);
    }
  }

  private void handleLanguage(DomElementVisitorContext context, String language) {
    String resource = context.getElement().getAttribute(CAMUNDA, "resource");
    if (StringUtils.isNotBlank(resource)) {
      context.addMessage(MessageFactory.resourceOnConditionalFlow(resource));
      return;
    }
    if ("feel".equalsIgnoreCase(language)) {
      handleFeelExpression(context);
      return;
    }
    context.addMessage(
        MessageFactory.scriptOnConditionalFlow(language, context.getElement().getTextContent()));
  }

  @Override
  protected Message cannotBeConvertedMessage(DomElementVisitorContext context) {
    return MessageFactory.conditionalFlow();
  }

  private void handleFeelExpression(DomElementVisitorContext context) {
    String oldExpression = context.getElement().getTextContent();
    String newExpression = "=" + oldExpression;
    context.addConversion(
        SequenceFlowConvertible.class, c -> c.setConditionExpression(newExpression));
    context.addMessage(MessageFactory.conditionExpressionFeel(oldExpression, newExpression));
  }

  private void handleJuelExpression(DomElementVisitorContext context) {
    String expression = context.getElement().getTextContent();
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform("Condition expression", expression);
    context.addConversion(
        SequenceFlowConvertible.class,
        conversion -> conversion.setConditionExpression(transformationResult.getFeelExpression()));
    context.addMessage(
        ExpressionTransformationResultMessageFactory.getMessage(
            transformationResult,
            "https://docs.camunda.io/docs/components/modeler/bpmn/exclusive-gateways/#conditions"));
  }
}
