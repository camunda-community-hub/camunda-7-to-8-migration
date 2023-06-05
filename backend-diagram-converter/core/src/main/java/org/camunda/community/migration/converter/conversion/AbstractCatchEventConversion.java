package org.camunda.community.migration.converter.conversion;

import static org.camunda.community.migration.converter.NamespaceUri.*;

import java.util.Optional;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.community.migration.converter.convertible.AbstractCatchEventConvertible;

public class AbstractCatchEventConversion
    extends AbstractTypedConversion<AbstractCatchEventConvertible> {
  @Override
  protected Class<AbstractCatchEventConvertible> type() {
    return AbstractCatchEventConvertible.class;
  }

  @Override
  protected void convertTyped(DomElement element, AbstractCatchEventConvertible convertible) {
    getTimerEventDefinition(element)
        .ifPresent(
            timerEventDefinition -> {
              getTimeDuration(timerEventDefinition)
                  .ifPresent(
                      timeDuration ->
                          applyExpression(timeDuration, convertible.getTimeDurationExpression()));
              getTimeDate(timerEventDefinition)
                  .ifPresent(
                      timeDate -> applyExpression(timeDate, convertible.getTimeDateExpression()));
              getTimeCycle(timerEventDefinition)
                  .ifPresent(
                      timeCycle ->
                          applyExpression(timeCycle, convertible.getTimeCycleExpression()));
            });
  }

  private void applyExpression(DomElement container, String expression) {
    if (expression != null) {
      container.setTextContent(expression);
    }
  }

  private Optional<DomElement> getTimerEventDefinition(DomElement element) {
    return getChildElement(element, BPMN, "timerEventDefinition");
  }

  private Optional<DomElement> getTimeDuration(DomElement timerEventDefinition) {
    return getChildElement(timerEventDefinition, BPMN, "timeDuration");
  }

  private Optional<DomElement> getTimeDate(DomElement timerEventDefinition) {
    return getChildElement(timerEventDefinition, BPMN, "timeDate");
  }

  private Optional<DomElement> getTimeCycle(DomElement timerEventDefinition) {
    return getChildElement(timerEventDefinition, BPMN, "timeCycle");
  }

  private Optional<DomElement> getChildElement(
      DomElement element, String namespaceUri, String localName) {
    return element.getChildElementsByNameNs(namespaceUri, localName).stream().findFirst();
  }
}
