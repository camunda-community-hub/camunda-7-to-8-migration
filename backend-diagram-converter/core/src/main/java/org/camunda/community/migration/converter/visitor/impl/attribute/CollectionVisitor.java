package org.camunda.community.migration.converter.visitor.impl.attribute;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractSupportedAttributeVisitor;

public class CollectionVisitor extends AbstractSupportedAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "collection";
  }

  @Override
  protected Message visitSupportedAttribute(DomElementVisitorContext context, String attribute) {
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(attribute);
    context.addConversion(
        AbstractActivityConvertible.class,
        AbstractActivityConvertible::initializeLoopCharacteristics);
    context.addConversion(
        AbstractActivityConvertible.class,
        conversion ->
            conversion
                .getBpmnMultiInstanceLoopCharacteristics()
                .getZeebeLoopCharacteristics()
                .setInputCollection(transformationResult.getFeelExpression()));
    context.addMessage(MessageFactory.collectionHint());
    Message message;
    if (transformationResult.hasExecutionOnly()) {
      message =
          MessageFactory.collectionExecution(
              attributeLocalName(),
              context.getElement().getLocalName(),
              transformationResult.getJuelExpression(),
              transformationResult.getFeelExpression());
    } else if (transformationResult.hasMethodInvocation()) {
      message =
          MessageFactory.collectionMethod(
              attributeLocalName(),
              context.getElement().getLocalName(),
              transformationResult.getJuelExpression(),
              transformationResult.getFeelExpression());
    } else {
      message =
          MessageFactory.collection(
              attributeLocalName(),
              context.getElement().getLocalName(),
              transformationResult.getJuelExpression(),
              transformationResult.getFeelExpression());
    }
    return message;
  }
}
