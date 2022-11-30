package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;

import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.BusinessRuleTaskConvertible;

public class BusinessRuleTaskConversion
    extends AbstractTypedConversion<BusinessRuleTaskConvertible> {

  private DomElement createCalledDecision(
      DomDocument document, BusinessRuleTaskConvertible convertible) {
    DomElement calledDecision = document.createElement(NamespaceUri.ZEEBE, "calledDecision");
    calledDecision.setAttribute("decisionId", convertible.getZeebeCalledDecision().getDecisionId());
    calledDecision.setAttribute(
        "resultVariable", convertible.getZeebeCalledDecision().getResultVariable());
    return calledDecision;
  }

  @Override
  protected void convertTyped(DomElement element, BusinessRuleTaskConvertible convertible) {
    DomElement extensionElements = getExtensionElements(element);
    extensionElements.appendChild(createCalledDecision(element.getDocument(), convertible));
  }

  @Override
  protected Class<BusinessRuleTaskConvertible> type() {
    return BusinessRuleTaskConvertible.class;
  }
}
