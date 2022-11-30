package org.camunda.community.migration.converter.visitor.impl.element;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractCamundaElementVisitor;

public class PropertiesVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "properties";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    return MessageFactory.properties();
  }

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return true;
  }

  @Override
  protected boolean isSilent() {
    return true;
  }
}
