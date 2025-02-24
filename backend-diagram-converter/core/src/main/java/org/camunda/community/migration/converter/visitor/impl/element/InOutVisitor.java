package org.camunda.community.migration.converter.visitor.impl.element;

import java.util.Optional;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.migration.converter.convertible.AbstractDataMapperConvertible.MappingDirection;
import org.camunda.community.migration.converter.convertible.CallActivityConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResultMessageFactory;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.version.SemanticVersion;
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
        if (SemanticVersion.parse(context.getProperties().getPlatformVersion()).ordinal()
            < SemanticVersion._8_3.ordinal()) {
          return MessageFactory.oldInAllHint();
        } else {
          context.addConversion(
              CallActivityConvertible.class,
              conversion ->
                  conversion.getZeebeCalledElement().setPropagateAllParentVariables(true));
          return MessageFactory.inAllHint();
        }
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
      ExpressionTransformationResult transformationResult =
          createResult(context.getElement(), target);
      context.addConversion(
          AbstractDataMapperConvertible.class,
          conversion ->
              conversion.addZeebeIoMapping(
                  getDirection(context.getElement()),
                  transformationResult.getFeelExpression(),
                  target));
      return ExpressionTransformationResultMessageFactory.getMessage(
          transformationResult,
          "https://docs.camunda.io/docs/components/modeler/bpmn/call-activities/#variable-mappings");
    }
  }

  private ExpressionTransformationResult createResult(DomElement element, String target) {
    String context = localName() + ", Parameter: '" + target + "'";
    String source = element.getAttribute("source");
    String sourceExpression = element.getAttribute("sourceExpression");
    if (source != null) {
      return new ExpressionTransformationResult(context, source, "=" + source, true, true);
    } else if (sourceExpression != null) {
      return ExpressionTransformer.transform(context, sourceExpression);
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
