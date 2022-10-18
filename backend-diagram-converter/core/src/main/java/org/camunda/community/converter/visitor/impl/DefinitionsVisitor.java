package org.camunda.community.converter.visitor.impl;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.visitor.AbstractElementVisitor;

public class DefinitionsVisitor extends AbstractElementVisitor {

  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    DomElement element = context.getElement();
    element.registerNamespace("zeebe", NamespaceUri.ZEEBE);
    element.registerNamespace("conversion", NamespaceUri.CONVERSION);
    element.setAttribute(NamespaceUri.MODELER, "executionPlatform", "Camunda Cloud");
    element.setAttribute(NamespaceUri.MODELER, "executionPlatformVersion", "8.0.0");
  }

  @Override
  protected String namespaceUri() {
    return NamespaceUri.BPMN;
  }

  @Override
  public String localName() {
    return "definitions";
  }
}
