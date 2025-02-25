package org.camunda.community.migration.converter.visitor.impl.dmn;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.convertible.TextConvertible;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResult;
import org.camunda.community.migration.converter.expression.ExpressionTransformationResultMessageFactory;
import org.camunda.community.migration.converter.expression.ExpressionTransformer;
import org.camunda.community.migration.converter.visitor.AbstractDmnElementVisitor;

public class TextVisitor extends AbstractDmnElementVisitor {

  @Override
  public String localName() {
    return "text";
  }

  @Override
  protected void visitDmnElement(DomElementVisitorContext context) {
    String content = context.getElement().getTextContent();
    ExpressionTransformationResult transform =
        ExpressionTransformer.transformDmn("Decision table field", content);
    context.addConversion(TextConvertible.class, c -> c.setContent(transform.getFeelExpression()));
    context.addMessage(ExpressionTransformationResultMessageFactory.getMessage(transform, null));
  }
}
