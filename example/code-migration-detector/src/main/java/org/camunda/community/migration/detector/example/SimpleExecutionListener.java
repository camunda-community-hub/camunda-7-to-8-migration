package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleExecutionListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution delegateExecution) throws Exception {}
}
