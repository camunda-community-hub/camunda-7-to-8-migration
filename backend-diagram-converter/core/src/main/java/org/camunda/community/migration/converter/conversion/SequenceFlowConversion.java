package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.SequenceFlowConvertible;

public class SequenceFlowConversion extends AbstractTypedConversion<SequenceFlowConvertible> {

  @Override
  protected void convertTyped(DomElement element, SequenceFlowConvertible convertible) {
    if (convertible.getConditionExpression() != null
        && convertible.getConditionExpression().trim().length() > 0) {
      getConditionExpression(element).setTextContent(convertible.getConditionExpression());
    }
  }

  @Override
  protected Class<SequenceFlowConvertible> type() {
    return SequenceFlowConvertible.class;
  }
}
