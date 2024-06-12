package org.camunda.community.migration.detector.rules.test;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

public class MyTaskListener implements TaskListener {
  @Override
  public void notify(DelegateTask delegateTask) {
    // do nothing
  }
}
