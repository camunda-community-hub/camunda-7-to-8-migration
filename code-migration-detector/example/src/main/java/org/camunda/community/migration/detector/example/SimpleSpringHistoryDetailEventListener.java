package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.impl.history.event.HistoricDetailEventEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleSpringHistoryDetailEventListener {

  @EventListener
  public void randomHistoryDetailEventName(HistoricDetailEventEntity historyEvent) {}
}
