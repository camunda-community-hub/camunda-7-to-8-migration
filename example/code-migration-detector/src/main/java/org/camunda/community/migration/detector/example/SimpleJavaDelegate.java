package org.camunda.community.migration.detector.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SimpleJavaDelegate implements JavaDelegate {
  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {}
}
