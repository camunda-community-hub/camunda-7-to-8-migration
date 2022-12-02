package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.NamespaceUri;

public abstract class AbstractBpmnAttributeVisitor extends AbstractAttributeVisitor {
  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  protected boolean removeAttribute() {
    return false;
  }
}
