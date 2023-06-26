package org.camunda.community.migration.converter.visitor.impl.element;

import java.util.Optional;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible.MappingDirection;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public abstract class InOutVisitor extends AbstractCamundaElementVisitor {
  private static final String IN = "in";
  private static final String OUT = "out";

  private boolean isIn(DomElement element) {
    return element.getLocalName().equals(IN);
  }

  private boolean isOut(DomElement element) {
    return element.getLocalName().equals(OUT);
  }

  private MappingDirection getDirection(DomElement element) {
    if (isIn(element)) {
      return MappingDirection.INPUT;
    } else if (isOut(element)) {
      return MappingDirection.OUTPUT;
    } else {
      throw mustBeInOrOut();
    }
  }

  private IllegalStateException mustBeInOrOut() {
    return new IllegalStateException("Must be in or out");
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    return !isBusinessKey(element);
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    boolean local =
        Boolean.getBoolean(
            Optional.ofNullable(element.getAttribute("local")).orElse(Boolean.toString(false)));
    if (local) {
      context.addMessage(MessageFactory.localVariablePropagationNotSupported());
    }
    if (isAll(context.getElement())) {
      if (isIn(context.getElement())) {
        return MessageFactory.inAllHint();
      } else if (isOut(context.getElement())) {
        context.addConversion(
            CallActivityConvertible.class,
            conversion -> conversion.getZeebeCalledElement().setPropagateAllChildVariables(true));
        return MessageFactory.outAllHint();
      } else {
        throw mustBeInOrOut();
      }
    } else if (isBusinessKey(context.getElement())) {
      return MessageFactory.inOutBusinessKeyNotSupported(context.getElement().getLocalName());
    } else {
      String target = element.getAttribute("target");
      ExpressionTransformationResult transformationResult = createResult(context.getElement());
      context.addConversion(
          AbstractDataMapperConvertible.class,
          conversion ->
              conversion.addZeebeIoMapping(
                  getDirection(context.getElement()),
                  transformationResult.getFeelExpression(),
                  target));
      return MessageFactory.inputOutputParameter(localName(), target, transformationResult);
    }
  }

  private ExpressionTransformationResult createResult(DomElement element) {
    String source = element.getAttribute("source");
    String sourceExpression = element.getAttribute("sourceExpression");
    if (source != null) {
      return new ExpressionTransformationResult(source, "=" + source);
    } else if (sourceExpression != null) {
      return ExpressionTransformer.transform(sourceExpression);
    } else {
      throw new IllegalStateException("Must have one of: 'source', 'sourceExpression'");
    }
  }

  private boolean isAll(DomElement element) {
    return "all".equals(element.getAttribute("variables"));
  }

  private boolean isBusinessKey(DomElement element) {
    return element.getAttribute("businessKey") != null;
  }

  public static class InVisitor extends InOutVisitor {

    @Override
    public String localName() {
      return IN;
    }
  }

  public static class OutVisitor extends InOutVisitor {
    @Override
    public String localName() {
      return OUT;
    }
  }
}
