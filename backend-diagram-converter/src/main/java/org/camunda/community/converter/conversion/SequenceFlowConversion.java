package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.SequenceFlowConvertible;

public class SequenceFlowConversion extends AbstractTypedConversion<SequenceFlowConvertible> {

  @Override
  protected void convertTyped(DomElement element, SequenceFlowConvertible convertible) {
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
                        .createElement(NamespaceUri.BPMN, "bpmn:conditionExpression");
                element.appendChild(conditionExpressionElement);
                conditionExpressionElement.setAttribute(
                    NamespaceUri.XSI, "type", "bpmn:tFormalExpression");
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
