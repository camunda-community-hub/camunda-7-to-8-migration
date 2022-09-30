package org.camunda.community.converter.visitor.impl;

import org.camunda.community.converter.BpmnDiagramCheckResult.Severity;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.ExpressionUtil;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.AbstractActivityConvertible;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class CompletionConditionVisitor extends AbstractElementVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    String textContent = context.getElement().getTextContent();
    String completionCondition = ExpressionUtil.transform(textContent, true).orElse(textContent);
    context.addConversion(
        AbstractActivityConvertible.class,
        conversion ->
            conversion
                .getBpmnMultiInstanceLoopCharacteristics()
                .setCompletionCondition(completionCondition));
    context.addMessage(
        Severity.TASK,
        "Completion condition was transformed. Please review: '" + completionCondition + "'");
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
