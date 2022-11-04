package org.camunda.community.converter.visitor.impl.element;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible.MappingDirection;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public abstract class InputOutputParameterVisitor extends AbstractCamundaElementVisitor {
  public static final String INPUT_PARAMETER = "inputParameter";
  public static final String OUTPUT_PARAMETER = "outputParameter";

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return !isNotStringOrExpression(context.getElement());
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    String name = element.getAttribute("name");
    MappingDirection direction = findMappingDirection(element);
    if (isNotStringOrExpression(element)) {
      return "'" + name + "': Only strings or expressions are supported as input/output in Zeebe";
    }
    String source = createSource(element, context);
    context.addConversion(
        AbstractDataMapperConvertible.class,
        abstractTaskConversion ->
            abstractTaskConversion.addZeebeIoMapping(direction, source, name));
    return "'" + name + "': Please review source '" + source + "' of " + direction + " " + name;
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

  private String createSource(DomElement element, DomElementVisitorContext context) {
    return ExpressionUtil.transform(element.getTextContent(), context);
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
