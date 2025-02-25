package org.camunda.community.migration.converter.visitor;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Arrays;
import java.util.List;
import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractAttributeVisitor extends AbstractFilteringVisitor {
  @Override
  protected void visitFilteredElement(DomElementVisitorContext context) {
    Attribute attribute = resolveAttribute(context);
    visitAttribute(context, attribute.value());
    if (removeAttribute(context) && attribute.namespaceUri() != null) {
      context.addAttributeToRemove(attributeLocalName(), attribute.namespaceUri());
    }
  }

  private Attribute resolveAttribute(DomElementVisitorContext context) {
    for (String namespaceUri : namespaceUri()) {
      String attribute = context.getElement().getAttribute(namespaceUri, attributeLocalName());
      if (attribute != null) {
        return new Attribute(namespaceUri, attributeLocalName(), attribute);
      }
    }
    return new Attribute(null, attributeLocalName(), null);
  }

  @Override
  protected boolean canVisit(DomElementVisitorContext context) {
    return resolveAttribute(context).value() != null
        && (context.getElement().getNamespaceURI().equals(BPMN)
            || Arrays.asList(DMN).contains(context.getElement().getNamespaceURI()));
  }

  protected abstract List<String> namespaceUri();

  public abstract String attributeLocalName();

  protected abstract void visitAttribute(DomElementVisitorContext context, String attribute);

  protected abstract boolean removeAttribute(DomElementVisitorContext context);

  @Override
  protected void logVisit(DomElementVisitorContext context) {
    LOG.debug(
        "Visiting attribute 'camunda:{}' on element '{}:{}'",
        attributeLocalName(),
        context.getElement().getPrefix(),
        context.getElement().getLocalName());
  }

  private record Attribute(String namespaceUri, String attributeLocalName, String value) {}
}
