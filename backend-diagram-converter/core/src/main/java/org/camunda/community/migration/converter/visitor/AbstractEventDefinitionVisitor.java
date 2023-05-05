package org.camunda.community.migration.converter.visitor;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractEventDefinitionVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected final void visitBpmnElement(DomElementVisitorContext context) {
    // do nothing
  }

  @Override
  protected String elementNameForMessage(DomElementVisitorContext context) {
    String fullEventName = "";
    if (!isInterrupting(context)) {
      fullEventName += "Non-interrupting ";
    }
    if (!isNotEventSubProcessStartEvent(context)) {
      fullEventName += "Event Sub Process ";
    }
    fullEventName += simpleEventName(context);
    fullEventName += " ";
    fullEventName +=
        StringUtils.capitalize(
            context.getElement().getParentElement().getLocalName().replaceAll("([A-Z])", " $1"));
    return fullEventName;
  }

  protected String simpleEventName(DomElementVisitorContext context) {
    return StringUtils.capitalize(context.getElement().getLocalName().split("([A-Z])")[0]);
  }

  protected boolean isNotEventSubProcessStartEvent(DomElementVisitorContext context) {
    if (context.getElement().getParentElement().getLocalName().equals("startEvent")) {
      boolean triggeredByEvent =
          parseWithDefault(
              context
                  .getElement()
                  .getParentElement()
                  .getParentElement()
                  .getAttribute(BPMN, "triggeredByEvent"),
              false);
      return !triggeredByEvent;
    }
    return true;
  }

  protected boolean isInterrupting(DomElementVisitorContext context) {
    boolean isInterrupting =
        parseWithDefault(
            context.getElement().getParentElement().getAttribute(BPMN, "isInterrupting"), true);
    boolean cancelActivity =
        parseWithDefault(
            context.getElement().getParentElement().getAttribute(BPMN, "cancelActivity"), true);

    return isInterrupting && cancelActivity;
  }

  private boolean parseWithDefault(String bool, boolean defaultValue) {
    return Optional.ofNullable(bool).map(Boolean::parseBoolean).orElse(defaultValue);
  }
}
