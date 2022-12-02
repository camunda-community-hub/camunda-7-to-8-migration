package org.camunda.community.migration.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.protocol.Protocol.*;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ZeebeSpringTest
public class ApplicationTest {

  @Autowired ZeebeClient zeebeClient;

  @Autowired ZeebeTestEngine zeebeTestEngine;

  @Test
  void shouldRunProcess_withUserTask() throws InterruptedException, TimeoutException {
    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("sample-process-solution-process")
            .latestVersion()
            .variables(Variables.createVariables().putValue("x", 7))
            .send()
            .join();
    waitForProcessInstanceHasPassedElement(processInstance, "main-process");
    zeebeTestEngine.waitForIdleState(Duration.ofMinutes(1));
    assertThat(processInstance).isWaitingAtElements("say-hello");
    List<ActivatedJob> userTasks =
        zeebeClient
            .newActivateJobsCommand()
            .jobType(USER_TASK_JOB_TYPE)
            .maxJobsToActivate(1)
            .send()
            .join()
            .getJobs();
    assertThat(userTasks).hasSize(1);
    zeebeClient.newCompleteCommand(userTasks.get(0)).send().join();
    waitForProcessInstanceCompleted(processInstance);
    assertThat(processInstance).isCompleted();
  }

  @Test
  void shouldRunProcess_withoutUserTask() throws InterruptedException, TimeoutException {
    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("sample-process-solution-process")
            .latestVersion()
            .variables(Variables.createVariables().putValue("x", 5))
            .send()
            .join();
    waitForProcessInstanceCompleted(processInstance);
    assertThat(processInstance).isCompleted().hasPassedElement("Event_15mbd4d");
  }
}
