package org.camunda.community.migration.converter.visitor;

import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractEventRefVisitor extends AbstractBpmnAttributeVisitor {

  @Override
  protected void visitAttribute(DomElementVisitorContext context, String attribute) {
    context.references(attribute);
  }

  protected boolean isEndEvent(DomElement element) {
    return element.getParentElement().getLocalName().equals("endEvent");
  }

  protected boolean isIntermediateThrowEvent(DomElement element) {
    return element.getParentElement().getLocalName().equals("intermediateThrowEvent");
  }
}
