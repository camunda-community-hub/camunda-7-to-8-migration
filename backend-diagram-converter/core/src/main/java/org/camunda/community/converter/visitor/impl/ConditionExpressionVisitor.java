package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class ConditionExpressionVisitor extends AbstractElementVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  public String localName() {
    return "conditionExpression";
  }

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    String transformedExpression =
        ExpressionUtil.transform(context.getElement().getTextContent(), true)
            .orElse(context.getElement().getTextContent());
    context.addConversion(
        SequenceFlowConvertible.class,
        conversion -> conversion.setConditionExpression(transformedExpression));
    context.addMessage(
        Severity.TASK,
        "Sequence Flow has condition expression set to '"
            + transformedExpression
            + "'. Please review");
  }
}
