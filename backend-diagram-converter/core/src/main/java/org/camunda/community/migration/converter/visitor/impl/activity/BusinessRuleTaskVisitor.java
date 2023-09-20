package org.camunda.community.migration.converter.visitor.impl.activity;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.convertible.BusinessRuleTaskConvertible;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ServiceTaskConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class BusinessRuleTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "businessRuleTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    if (isDmnImplementation(context)) {
      return new BusinessRuleTaskConvertible();
    } else {
      return new ServiceTaskConvertible();
    }
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return SemanticVersion._8_0_0;
  }

  public static boolean isDmnImplementation(DomElementVisitorContext context) {
    return context.getElement().getAttribute(NamespaceUri.CAMUNDA, "decisionRef") != null;
  }
}
