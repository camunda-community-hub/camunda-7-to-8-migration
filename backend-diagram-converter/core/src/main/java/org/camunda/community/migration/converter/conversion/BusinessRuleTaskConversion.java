package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.BpmnElementFactory.*;
import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.BusinessRuleTaskConvertible;

public class BusinessRuleTaskConversion
    extends AbstractTypedConversion<BusinessRuleTaskConvertible> {

  private DomElement createCalledDecision(
      DomDocument document, BusinessRuleTaskConvertible convertible) {
    DomElement calledDecision = document.createElement(ZEEBE, "calledDecision");
    calledDecision.setAttribute(
        ZEEBE, "decisionId", convertible.getZeebeCalledDecision().getDecisionId());
    if (convertible.getZeebeCalledDecision().getBindingType() != null) {
      calledDecision.setAttribute(
          ZEEBE, "bindingType", convertible.getZeebeCalledDecision().getBindingType().name());
    }
    if (StringUtils.isNotBlank(convertible.getZeebeCalledDecision().getVersionTag())) {
      calledDecision.setAttribute(
          ZEEBE, "versionTag", convertible.getZeebeCalledDecision().getVersionTag());
    }
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
