package org.camunda.community.converter.visitor.impl.element;

import java.util.Optional;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible;
import org.camunda.community.converter.convertible.AbstractDataMapperConvertible.MappingDirection;
import org.camunda.community.converter.convertible.CallActivityConvertible;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public abstract class InOutVisitor extends AbstractCamundaElementVisitor {
  private static final String IN = "in";
  private static final String OUT = "out";

  private boolean isIn(DomElement element) {
    return element.getLocalName().equals(IN);
  }

  private boolean isOut(DomElement element) {
    return element.getLocalName().equals(OUT);
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    return !(isIn(element) && isAll(element)) && !isBusinessKey(element);
  }

  @Override
  protected String visitCamundaElement(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    boolean local =
        Boolean.getBoolean(
            Optional.ofNullable(element.getAttribute("local")).orElse(Boolean.toString(false)));
    if (local) {
      context.addMessage(
          Severity.TASK,
          "Local variable propagation is not supported any more. Please review your variable mapping");
    }
    StringBuilder builder = new StringBuilder();
    handleIfAll(element, context).ifPresent(builder::append);
    handleIfSource(element, context).ifPresent(builder::append);
    handleIfSourceExpression(element, context).ifPresent(builder::append);
    handleIfBusinessKey(element).ifPresent(builder::append);
    return builder.toString();
  }

  private Optional<String> handleIfBusinessKey(DomElement element) {
    if (isBusinessKey(element)) {
      return Optional.of("Business Keys are not supported in Zeebe");
    } else {
      return Optional.empty();
    }
  }

  private boolean isAll(DomElement element) {
    return "all".equals(element.getAttribute("variables"));
  }

  private boolean isBusinessKey(DomElement element) {
    return element.getAttribute("businessKey") != null;
  }

  private Optional<String> handleIfAll(DomElement element, DomElementVisitorContext result) {
    if (isAll(element)) {
      if (isIn(element)) {
        return Optional.of("Cannot propagate all variables into call activity");
      } else if (isOut(element)) {
        result.addConversion(
            CallActivityConvertible.class,
            conversion -> conversion.getZeebeCalledElement().setPropagateAllChildVariables(true));
        return Optional.of("Propagating all child values out is not recommended");
      } else {
        throw new IllegalStateException("Must be in or out");
      }
    } else {
      return Optional.empty();
    }
  }

  private Optional<String> handleIfSource(DomElement element, DomElementVisitorContext result) {
    String source = element.getAttribute("source");
    String target = element.getAttribute("target");
    if (source != null) {
      String expressionSource = "=" + source;
      result.addConversion(
          AbstractDataMapperConvertible.class,
          conversion ->
              conversion.addZeebeIoMapping(MappingDirection.INPUT, expressionSource, target));
      return Optional.of(
          "Call Activity in mapping '"
              + target
              + "' was translated to mapping. Please review the mappings");
    } else {
      return Optional.empty();
    }
  }

  private Optional<String> handleIfSourceExpression(
      DomElement element, DomElementVisitorContext result) {
    String sourceExpression = element.getAttribute("sourceExpression");
    String target = element.getAttribute("target");
    if (sourceExpression != null) {
      String translatedSourceExpression =
          ExpressionUtil.transform(sourceExpression, true).orElse(sourceExpression);
      result.addConversion(
          AbstractDataMapperConvertible.class,
          conversion ->
              conversion.addZeebeIoMapping(
                  MappingDirection.INPUT, translatedSourceExpression, target));
      if (translatedSourceExpression.equals(sourceExpression)) {
        return Optional.of(
            "Could not translate expression '"
                + translatedSourceExpression
                + "' on mapping '"
                + target
                + "'. Please provide a proper translation");
      } else {
        return Optional.of(
            "Call Activity mapping '"
                + target
                + "' was translated to mapping using an expression. Please review the mappings");
      }
    } else {
      return Optional.empty();
    }
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
