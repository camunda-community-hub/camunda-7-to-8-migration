package org.camunda.community.migration.converter;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;

@ZeebeProcessTest
public class BehaviourTest {
  private static final String CALL_ACTIVITY_BEHAVIOUR = "behaviour/call-activity.bpmn";

  ZeebeTestEngine engine;
  ZeebeClient client;
  RecordStream recordStream;

  @Test
  public void testCallActivityBehaviour() throws InterruptedException, TimeoutException {
    BpmnConverter converter = BpmnConverterFactory.getInstance().get();
    ConverterProperties properties = ConverterPropertiesFactory.getInstance().get();
    BpmnModelInstance modelInstance = BpmnModelInstanceUtil.fromResource(CALL_ACTIVITY_BEHAVIOUR);
    converter.convert(modelInstance, false, properties);
    client
        .newDeployResourceCommand()
        .addProcessModel(BpmnModelInstanceUtil.transform(modelInstance), "test.bpmn")
        .send()
        .join();
    Map<String, Object> variables = TestUtil.mockVariables(5);
    ProcessInstanceEvent callingProcess =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("CallingProcess")
            .latestVersion()
            .variables(variables)
            .send()
            .join();
    engine.waitForIdleState(Duration.ofSeconds(60));
    BpmnAssert.assertThat(callingProcess).hasCalledProcess("AnotherProcess");

    InspectedProcessInstance anotherProcess =
        InspectionUtility.findProcessInstances()
            .withParentProcessInstanceKey(callingProcess.getProcessInstanceKey())
            .withBpmnProcessId("AnotherProcess")
            .findFirstProcessInstance()
            .get();
    BpmnAssert.assertThat(anotherProcess).isWaitingAtElements("DoSomethingTask");
    ActivatedJob activatedJob =
        client
            .newActivateJobsCommand()
            .jobType("do-something")
            .maxJobsToActivate(1)
            .send()
            .join()
            .getJobs()
            .get(0);
    BpmnAssert.assertThat(activatedJob).extractingVariables().containsAllEntriesOf(variables);
    Map<String, Object> doSomethingResult = TestUtil.mockVariables(3);
    client.newCompleteCommand(activatedJob).variables(doSomethingResult).send().join();
    engine.waitForIdleState(Duration.ofSeconds(60));

    BpmnAssert.assertThat(anotherProcess).isCompleted();
    BpmnAssert.assertThat(callingProcess).isWaitingAtElements("DoSomethingElseTask");
    ActivatedJob anotherJob =
        client
            .newActivateJobsCommand()
            .jobType("do-something-else")
            .maxJobsToActivate(1)
            .send()
            .join()
            .getJobs()
            .get(0);
    BpmnAssert.assertThat(anotherJob)
        .extractingVariables()
        .containsAllEntriesOf(variables)
        .containsAllEntriesOf(doSomethingResult);
    client.newCompleteCommand(anotherJob).send().join();
    engine.waitForIdleState(Duration.ofSeconds(60));

    BpmnAssert.assertThat(callingProcess).isCompleted();
  }
}
