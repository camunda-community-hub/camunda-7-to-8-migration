package org.camunda.community.migration.converter.visitor;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.DomElementVisitorContext;

public abstract class AbstractEventDefinitionVisitor extends AbstractBpmnElementVisitor {

  @Override
  protected final void visitBpmnElement(DomElementVisitorContext context) {
    // do nothing
  }

  @Override
  protected String elementNameForMessage(DomElement element) {
    String fullEventName = "";
    if (!isInterrupting(element)) {
      fullEventName += "Non-interrupting ";
    }
    if (!isNotEventSubProcessStartEvent(element)) {
      fullEventName += "Event Sub Process ";
    }
    fullEventName += simpleEventName(element);
    fullEventName += " ";
    fullEventName +=
        StringUtils.capitalize(
            element.getParentElement().getLocalName().replaceAll("([A-Z])", " $1"));
    return fullEventName;
  }

  protected String simpleEventName(DomElement element) {
    return StringUtils.capitalize(element.getLocalName().split("([A-Z])")[0]);
  }

  protected boolean isNotEventSubProcessStartEvent(DomElement element) {
    if (element.getParentElement().getLocalName().equals("startEvent")) {
      boolean triggeredByEvent =
          parseWithDefault(
              element.getParentElement().getParentElement().getAttribute(BPMN, "triggeredByEvent"),
              false);
      return !triggeredByEvent;
    }
    return true;
  }

  protected boolean isInterrupting(DomElement element) {
    boolean isInterrupting =
        parseWithDefault(element.getParentElement().getAttribute(BPMN, "isInterrupting"), true);
    boolean cancelActivity =
        parseWithDefault(element.getParentElement().getAttribute(BPMN, "cancelActivity"), true);

    return isInterrupting && cancelActivity;
  }

  private boolean parseWithDefault(String bool, boolean defaultValue) {
    return Optional.ofNullable(bool).map(Boolean::parseBoolean).orElse(defaultValue);
  }
}
