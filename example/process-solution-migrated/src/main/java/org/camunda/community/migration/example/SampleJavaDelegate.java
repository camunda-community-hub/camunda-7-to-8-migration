package org.camunda.community.migration.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SampleJavaDelegate implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    System.out.println("SampleJavaDelegate " + execution.getVariable("x"));
    execution.setVariable("y", "hello world");
  }
}
