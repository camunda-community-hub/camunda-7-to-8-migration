package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleSpringExecutionListener {

  @EventListener
  public void randomExecutionName(DelegateExecution delegateExecution) {}
}
