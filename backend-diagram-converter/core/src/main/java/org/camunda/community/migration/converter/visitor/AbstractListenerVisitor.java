package org.camunda.community.migration.converter.visitor;

import org.camunda.community.migration.converter.DomElementVisitorContext;
import org.camunda.community.migration.converter.NamespaceUri;
import org.camunda.community.migration.converter.message.Message;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor.ListenerImplementation.ClassImplementation;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor.ListenerImplementation.DelegateExpressionImplementation;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor.ListenerImplementation.ExpressionImplementation;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor.ListenerImplementation.NullImplementation;
import org.camunda.community.migration.converter.visitor.AbstractListenerVisitor.ListenerImplementation.ScriptImplementation;

public abstract class AbstractListenerVisitor extends AbstractCamundaElementVisitor {

  @Override
  protected final Message visitCamundaElement(DomElementVisitorContext context) {
    ListenerImplementation implementation = findListenerImplementation(context);
    String event = context.getElement().getAttribute(NamespaceUri.CAMUNDA, "event");
    return visitListener(context, event, implementation);
  }

  protected abstract Message visitListener(
      DomElementVisitorContext context, String event, ListenerImplementation implementation);

  private ListenerImplementation findListenerImplementation(DomElementVisitorContext context) {
    String listenerImplementation = context.getElement().getAttribute("delegateExpression");
    if (listenerImplementation != null) {
      return new DelegateExpressionImplementation(listenerImplementation);
    }

    listenerImplementation = context.getElement().getAttribute("class");
    if (listenerImplementation != null) {
      return new ClassImplementation(listenerImplementation);
    }
    listenerImplementation = context.getElement().getAttribute("expression");
    if (listenerImplementation != null) {
      return new ExpressionImplementation(listenerImplementation);
    }
    if (context.getElement().getChildElementsByNameNs(NamespaceUri.CAMUNDA, "script") != null
        && !context
            .getElement()
            .getChildElementsByNameNs(NamespaceUri.CAMUNDA, "script")
            .isEmpty()) {
      listenerImplementation =
          context
              .getElement()
              .getChildElementsByNameNs(NamespaceUri.CAMUNDA, "script")
              .get(0)
              .getAttribute("scriptFormat");
      return new ScriptImplementation(listenerImplementation);
    }
    return new NullImplementation();
  }

  public sealed interface ListenerImplementation {
    String implementation();

    record DelegateExpressionImplementation(String implementation)
        implements ListenerImplementation {}

    record ClassImplementation(String implementation) implements ListenerImplementation {}

    record ExpressionImplementation(String implementation) implements ListenerImplementation {}

    record ScriptImplementation(String implementation) implements ListenerImplementation {}

    record NullImplementation() implements ListenerImplementation {
      @Override
      public String implementation() {
        return null;
      }
    }
  }
}
