package org.camunda.community.converter.conversion;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.ConverterProperties;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;

public class BusinessRuleTaskConversion
    extends AbstractTypedConversion<BusinessRuleTaskConvertible> {

  private DomElement createCalledDecision(
      DomDocument document,
      BusinessRuleTaskConvertible convertible,
      ConverterProperties properties) {
    DomElement calledDecision =
        document.createElement(properties.getZeebeNamespace().getUri(), "calledDecision");
    calledDecision.setAttribute("decisionId", convertible.getZeebeCalledDecision().getDecisionId());
    calledDecision.setAttribute(
        "resultVariable", convertible.getZeebeCalledDecision().getResultVariable());
    return calledDecision;
  }

  @Override
  protected void convertTyped(
      DomElement element, BusinessRuleTaskConvertible convertible, ConverterProperties properties) {
    DomElement extensionElements = getExtensionElements(element, properties);
    extensionElements.appendChild(
        createCalledDecision(element.getDocument(), convertible, properties));
  }

  @Override
  protected Class<BusinessRuleTaskConvertible> type() {
    return BusinessRuleTaskConvertible.class;
  }
}
