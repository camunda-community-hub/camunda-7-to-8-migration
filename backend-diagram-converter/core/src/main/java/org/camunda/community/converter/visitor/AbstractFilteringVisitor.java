package org.camunda.community.converter.visitor;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.exception.VisitorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilteringVisitor implements DomElementVisitor {
  protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Override
  public void visit(DomElementVisitorContext context) {
    try {
      if (canVisit(context)) {
        logVisit(context);
        visitFilteredElement(context);
      }
    } catch (Exception e) {
      throw new VisitorException(this.getClass(), context.getElement(), e);
    }
  }

  protected abstract void visitFilteredElement(DomElementVisitorContext context);

  protected abstract boolean canVisit(DomElementVisitorContext context);

  protected void logVisit(DomElementVisitorContext context) {
    LOG.debug(
        "Visiting {}:{}", context.getElement().getPrefix(), context.getElement().getLocalName());
  }
}
