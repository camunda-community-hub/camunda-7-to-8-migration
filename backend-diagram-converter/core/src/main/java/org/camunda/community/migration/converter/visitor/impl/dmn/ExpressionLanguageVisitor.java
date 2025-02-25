package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractDmnAttributeVisitor;

public class ExpressionLanguageVisitor extends AbstractDmnAttributeVisitor {
  @Override
  public String attributeLocalName() {
    return "expressionLanguage";
  }

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    if (context.getElement().getLocalName().equals("definitions")) {
      return;
    }
    context.addAttributeToRemove(attributeLocalName(), context.getElement().getNamespaceURI());
    context.addMessage(MessageFactory.onlyFeelSupported());
  }
}
