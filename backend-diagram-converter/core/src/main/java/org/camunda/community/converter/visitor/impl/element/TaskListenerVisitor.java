package org.camunda.community.converter.visitor.impl.element;

import org.camunda.community.converter.DomElementVisitorContext;
import org.camunda.community.converter.NamespaceUri;
import org.camunda.community.converter.message.Message;
import org.camunda.community.converter.message.MessageFactory;
import org.camunda.community.converter.visitor.AbstractCamundaElementVisitor;

public class TaskListenerVisitor extends AbstractCamundaElementVisitor {
  @Override
  public String localName() {
    return "taskListener";
  }

  @Override
  protected Message visitCamundaElement(DomElementVisitorContext context) {
    String listenerImplementation = findListenerImplementation(context);
    return MessageFactory.taskListener(listenerImplementation);
  }

  protected String findListenerImplementation(DomElementVisitorContext context) {
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

  @Override
  public boolean canBeTransformed(DomElementVisitorContext context) {
    return false;
  }
}
