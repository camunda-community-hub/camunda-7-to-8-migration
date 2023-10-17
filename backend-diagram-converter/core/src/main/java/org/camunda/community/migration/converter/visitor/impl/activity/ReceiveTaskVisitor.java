package org.camunda.community.migration.converter.visitor.impl.activity;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.Convertible;
import org.camunda.community.migration.converter.convertible.ReceiveTaskConvertible;
import org.camunda.community.migration.converter.version.SemanticVersion;
import org.camunda.community.migration.converter.visitor.AbstractActivityVisitor;

public class ReceiveTaskVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "receiveTask";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new ReceiveTaskConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    String instantiate = context.getElement().getAttribute(BPMN, "instantiate");
    if (Boolean.parseBoolean(instantiate)) {
      return null;
    }
    return SemanticVersion._8_0;
  }
}
