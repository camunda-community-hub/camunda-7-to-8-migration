package org.camunda.community.migration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component("sampleBean")
public class SampleBean {
  public static boolean executionReceived = false;
  public static boolean someVariableReceived = false;

  public void doStuff(DelegateExecution execution, String someVariable) {
    executionReceived = execution != null;
    someVariableReceived = someVariable != null;
  }
}
