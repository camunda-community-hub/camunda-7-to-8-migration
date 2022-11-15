package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

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
