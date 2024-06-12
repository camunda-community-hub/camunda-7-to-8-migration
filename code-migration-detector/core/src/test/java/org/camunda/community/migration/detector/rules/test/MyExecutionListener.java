package org.camunda.community.migration.detector.rules.test;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class MyExecutionListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution delegateExecution) throws Exception {
    // do nothing
  }
}
