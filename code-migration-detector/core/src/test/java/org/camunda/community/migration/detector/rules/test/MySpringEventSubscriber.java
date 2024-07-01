package org.camunda.community.migration.detector.rules.test;

import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.context.event.EventListener;

public class MySpringEventSubscriber {
  @EventListener
  public void handleTask(TaskEvent taskEvent) {
    // do nothing
  }
}
