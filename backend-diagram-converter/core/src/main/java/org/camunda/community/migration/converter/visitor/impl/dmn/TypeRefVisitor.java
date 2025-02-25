package org.camunda.community.migration.converter.visitor.impl.dmn;

import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.AbstractTypeRefConvertible;
import org.camunda.community.migration.converter.message.MessageFactory;
import org.camunda.community.migration.converter.visitor.AbstractDmnAttributeVisitor;

public class TypeRefVisitor extends AbstractDmnAttributeVisitor {
  private static final List<String> NUMBER_TYPES = List.of("double", "long");

  @Override
  public String attributeLocalName() {
    return "typeRef";
  }

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    if (NUMBER_TYPES.contains(attribute)) {
      context.addConversion(AbstractTypeRefConvertible.class, c -> c.setTypeRef("number"));
      context.addMessage(MessageFactory.numberType());
    }
  }
}
