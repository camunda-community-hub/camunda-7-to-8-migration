package org.camunda.community.migration.converter.visitor;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Arrays;
import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractDmnElementVisitor extends AbstractElementVisitor {
  @Override
  protected List<String> namespaceUri() {
    return Arrays.asList(DMN);
  }

  @Override
  protected final void visitElement(DomElementVisitorContext context) {
    visitDmnElement(context);
  }

  protected abstract void visitDmnElement(DomElementVisitorContext context);
}
