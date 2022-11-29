package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class BusinessRuleTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "businessRuleTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    if (isDmnImplementation(context.getElement(), context)) {
      return new BusinessRuleTaskConvertible();
    } else {
      return new ServiceTaskConvertible();
    }
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  private boolean isDmnImplementation(DomElement element, DomElementVisitorContext context) {
    return element.getAttribute(NamespaceUri.CAMUNDA, "decisionRef") != null;
  }
}
