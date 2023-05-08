package org.camunda.community.migration.processInstance.exporter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SampleDelegate implements JavaDelegate {
  @Override
  public void execute(DelegateExecution execution) throws Exception {}
}
