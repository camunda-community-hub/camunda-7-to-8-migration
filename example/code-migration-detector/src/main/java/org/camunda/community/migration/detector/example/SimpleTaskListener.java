package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleTaskListener implements TaskListener {
  @Override
  public void notify(DelegateTask delegateTask) {}
}
