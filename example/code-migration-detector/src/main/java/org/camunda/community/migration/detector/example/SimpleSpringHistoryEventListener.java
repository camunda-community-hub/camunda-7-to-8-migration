package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleSpringHistoryEventListener {

  @EventListener
  public void randomHistoryEventName(HistoryEvent historyEvent) {}
}
