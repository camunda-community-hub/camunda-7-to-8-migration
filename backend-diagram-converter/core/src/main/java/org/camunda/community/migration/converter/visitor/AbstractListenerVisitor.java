package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.message.Message;

public abstract class AbstractListenerVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected final Message visitCamundaElement(DomElementVisitorContext context) {
    String implementation = findListenerImplementation(context);
    String event = context.getElement().getAttribute(NamespaceUri.CAMUNDA, "event");
    return visitListener(context, event, implementation);
  }

  protected abstract Message visitListener(
      DomElementVisitorContext context, String event, String implementation);

  private String findListenerImplementation(DomElementVisitorContext context) {
    String listenerImplementation = context.getElement().getAttribute("delegateExpression");
    if (listenerImplementation == null) {
      listenerImplementation = context.getElement().getAttribute("class");
    }
    if (listenerImplementation == null) {
      listenerImplementation = context.getElement().getAttribute("expression");
    }
    if (listenerImplementation == null
        && context.getElement().getChildElementsByNameNs(NamespaceUri.CAMUNDA, "script") != null) {
      listenerImplementation =
          context
              .getElement()
              .getChildElementsByNameNs(NamespaceUri.CAMUNDA, "script")
              .get(0)
              .getAttribute("scriptFormat");
    }
    return listenerImplementation;
  }
}
