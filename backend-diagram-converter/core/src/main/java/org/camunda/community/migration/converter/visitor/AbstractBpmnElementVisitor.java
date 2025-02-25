package org.camunda.community.migration.converter.visitor;

import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;

public abstract class AbstractBpmnElementVisitor extends AbstractElementVisitor {
  @Override
  protected final List<String> namespaceUri() {
    return List.of(NamespaceUri.BPMN);
  }

  @Override
  protected void visitElement(DomElementVisitorContext context) {
    visitBpmnElement(context);
  }

  protected abstract void visitBpmnElement(DomElementVisitorContext context);
}
