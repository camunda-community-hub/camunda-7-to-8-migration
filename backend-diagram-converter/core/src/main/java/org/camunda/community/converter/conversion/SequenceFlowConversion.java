package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;

public class SequenceFlowConversion extends AbstractTypedConversion<SequenceFlowConvertible> {

  @Override
  protected void convertTyped(
      DomElement element, SequenceFlowConvertible convertible, ConverterProperties properties) {
    if (convertible.getConditionExpression() != null
        && convertible.getConditionExpression().trim().length() > 0) {
      element.getChildElements().stream()
          .filter(e -> e.getLocalName().equals("conditionExpression"))
          .findFirst()
          .orElseGet(
              () -> {
                DomElement conditionExpressionElement =
                    element
                        .getDocument()
                        .createElement(
                            properties.getBpmnNamespace().getUri(), "bpmn:conditionExpression");
                element.appendChild(conditionExpressionElement);
                conditionExpressionElement.setAttribute(
                    properties.getXsiNamespace().getUri(), "type", "bpmn:tFormalExpression");
                return conditionExpressionElement;
              })
          .setTextContent(convertible.getConditionExpression());
    }
  }

  @Override
  protected Class<SequenceFlowConvertible> type() {
    return SequenceFlowConvertible.class;
  }
}
