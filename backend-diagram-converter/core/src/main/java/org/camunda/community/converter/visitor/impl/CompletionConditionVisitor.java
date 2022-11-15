package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.converter.expression.ExpressionTransformationResult;
import org.camunda.community.converter.expression.ExpressionTransformer;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class CompletionConditionVisitor extends AbstractElementVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    String textContent = context.getElement().getTextContent();
    ExpressionTransformationResult transformationResult =
        ExpressionTransformer.transform(textContent);
    context.addConversion(
        AbstractActivityConvertible.class,
        conversion ->
            conversion
                .getBpmnMultiInstanceLoopCharacteristics()
                .setCompletionCondition(transformationResult.getNewExpression()));
    context.addMessage(Severity.TASK, MessageFactory.completionCondition(transformationResult));
  }

  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  public String localName() {
    return "completionCondition";
  }
}
