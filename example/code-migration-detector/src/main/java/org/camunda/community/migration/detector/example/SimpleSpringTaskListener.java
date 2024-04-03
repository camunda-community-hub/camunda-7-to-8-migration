package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleSpringTaskListener {

  @EventListener
  public void randomName(DelegateTask delegateTask) {}
}
