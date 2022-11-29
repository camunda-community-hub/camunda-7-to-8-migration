package org.camunda.community.converter.visitor.impl.activity;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.convertible.Convertible;
import org.camunda.community.converter.convertible.TransactionConvertible;
import org.camunda.community.converter.version.SemanticVersion;
import org.camunda.community.converter.visitor.AbstractActivityVisitor;

public class TransactionVisitor extends AbstractActivityVisitor {
  @Override
  public String localName() {
    return "transaction";
  }

  @Override
  protected Convertible createConvertible(DomElementVisitorContext context) {
    return new TransactionConvertible();
  }

  @Override
  protected SemanticVersion availableFrom(DomElementVisitorContext context) {
    return null;
  }
}
