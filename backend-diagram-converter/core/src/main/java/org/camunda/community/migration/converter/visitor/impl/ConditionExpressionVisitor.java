package org.camunda.community.migration.converter.visitor.impl;

import java.util.Arrays;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
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
    return isConditionalFlow(context) ? null : SemanticVersion._8_0_0;
  }

  private boolean isConditionalFlow(DomElementVisitorContext context) {
    String sourceRef =
        context.getElement().getParentElement().getAttribute(NamespaceUri.BPMN, "sourceRef");
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
    return element.getNamespaceURI().equals(NamespaceUri.BPMN)
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
    String expression = context.getElement().getTextContent();
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(expression);
    context.addConversion(
        SequenceFlowConvertible.class,
        conversion -> conversion.setConditionExpression(transformationResult.getFeelExpression()));
    if (transformationResult.hasExecution()) {
      context.addMessage(MessageFactory.conditionExpressionExecution(transformationResult));
    } else if (transformationResult.hasMethodInvocation()) {
      context.addMessage(MessageFactory.conditionExpressionMethod(transformationResult));
    } else {
      context.addMessage(MessageFactory.conditionExpression(transformationResult));
    }
  }

  @Override
  protected Message cannotBeConvertedMessage(DomElementVisitorContext context) {
    return MessageFactory.conditionalFlow();
  }
}
