package org.camunda.community.migration.example;

import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.response.ProcessInstanceEvent;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@CamundaSpringProcessTest
public class ApplicationTest {

  @Autowired
  CamundaClient camundaClient;

  @Test
  void shouldRunProcess_withUserTask() throws InterruptedException, TimeoutException {
    ProcessInstanceEvent processInstance = camundaClient
        .newCreateInstanceCommand()
        .bpmnProcessId("sample-process-solution-process")
        .latestVersion()
        .variables(Variables
            .createVariables()
            .putValue("x", 7))
        .send()
        .join();
    CamundaAssert
        .assertThat(processInstance)
        .hasCompletedElements("Sub Process");
    CamundaAssert
        .assertThat(processInstance)
        .hasActiveElements("Say hello to\n" + "demo");
    List<ActivatedJob> userTasks = camundaClient
        .newActivateJobsCommand()
        .jobType(USER_TASK_JOB_TYPE)
        .maxJobsToActivate(100)
        .send()
        .join()
        .getJobs()
        .stream()
        .filter(j -> j.getProcessInstanceKey() == processInstance.getProcessInstanceKey())
        .toList();
    assertThat(userTasks).hasSize(1);
    camundaClient
        .newCompleteCommand(userTasks.get(0))
        .send()
        .join();
    CamundaAssert
        .assertThat(processInstance)
        .hasCompletedElements("Say hello to\n" + "demo")
        .isCompleted();
  }

  @Test
  void shouldRunProcess_withoutUserTask() throws InterruptedException, TimeoutException {
    ProcessInstanceEvent processInstance = camundaClient
        .newCreateInstanceCommand()
        .bpmnProcessId("sample-process-solution-process")
        .latestVersion()
        .variables(Variables
            .createVariables()
            .putValue("x", 5))
        .send()
        .join();
    CamundaAssert
        .assertThat(processInstance)
        .isCompleted();
  }
}
