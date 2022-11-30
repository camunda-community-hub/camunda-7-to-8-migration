package org.camunda.community.migration.example;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
public class ApplicationTest {
  @Test
  void shouldRunProcess_withUserTask() {
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceByKey(
                "sample-process-solution-process", Variables.createVariables().putValue("x", 7));
    assertThat(processInstance).isWaitingAt(findId("Say hello to demo"));
    complete(task());
    assertThat(processInstance).isEnded();
  }

  @Test
  void shouldRunProcess_withoutUserTask() {
    ProcessInstance processInstance =
        runtimeService()
            .startProcessInstanceByKey(
                "sample-process-solution-process", Variables.createVariables().putValue("x", 5));
    assertThat(processInstance).isEnded().hasPassed("Event_15mbd4d");
  }
}
