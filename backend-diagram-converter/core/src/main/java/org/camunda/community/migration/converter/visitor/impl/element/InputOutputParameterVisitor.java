package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible.MappingDirection;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public abstract class InputOutputParameterVisitor extends AbstractCamundaElementVisitor {
  public static final String INPUT_PARAMETER = "inputParameter";
  public static final String OUTPUT_PARAMETER = "outputParameter";

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return !isNotStringOrExpression(context.getElement());
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    String name = element.getAttribute("name");
    MappingDirection direction = findMappingDirection(element);
    if (isNotStringOrExpression(element)) {
      return MessageFactory.inputOutputParameterIsNoExpression(localName(), name);
    }
    String expression = element.getTextContent();
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(expression);
    context.addConversion(
        AbstractDataMapperConvertible.class,
        abstractTaskConversion ->
            abstractTaskConversion.addZeebeIoMapping(
                direction, transformationResult.getFeelExpression(), name));
    Message resultMessage;
    if (transformationResult.hasExecution()) {
      resultMessage =
          MessageFactory.inputOutputParameterExecution(localName(), name, transformationResult);
    } else if (transformationResult.hasMethodInvocation()) {
      resultMessage =
          MessageFactory.inputOutputParameterMethod(localName(), name, transformationResult);
    } else {
      resultMessage = MessageFactory.inputOutputParameter(localName(), name, transformationResult);
    }
    return resultMessage;
  }

  private MappingDirection findMappingDirection(DomElement element) {
    if (isInputParameter(element.getLocalName())) {
      return MappingDirection.INPUT;
    }
    if (isOutputParameter(element.getLocalName())) {
      return MappingDirection.OUTPUT;
    }
    throw new IllegalStateException("Must be input or output!");
  }

  private boolean isNotStringOrExpression(DomElement element) {
    return element.getChildElements().size() > 0;
  }

  private boolean isInputParameter(String localName) {
    return INPUT_PARAMETER.equals(localName);
  }

  private boolean isOutputParameter(String localName) {
    return OUTPUT_PARAMETER.equals(localName);
  }

  public static class InputParameterVisitor extends InputOutputParameterVisitor {

    @Override
    public String localName() {
      return INPUT_PARAMETER;
    }
  }

  public static class OutputParameterVisitor extends InputOutputParameterVisitor {

    @Override
    public String localName() {
      return OUTPUT_PARAMETER;
    }
  }
}
