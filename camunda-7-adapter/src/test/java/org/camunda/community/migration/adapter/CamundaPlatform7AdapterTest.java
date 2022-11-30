package org.camunda.community.migration.adapter;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CamundaPlatform7AdapterConfig.class)
@ZeebeSpringTest
public class CamundaPlatform7AdapterTest {

  @Autowired private ZeebeClient zeebeClient;

  @Test
  public void testDelegateClass() {
    BpmnModelInstance bpmn =
        Bpmn.createExecutableProcess("test")
            .startEvent()
            .serviceTask()
            .zeebeJobType("camunda-7-adapter")
            .zeebeTaskHeader("class", SampleDelegate.class.getName())
            .endEvent()
            .done();

    zeebeClient.newDeployResourceCommand().addProcessModel(bpmn, "test.bpmn").send().join();

    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("test")
            .latestVersion()
            .variables(Collections.singletonMap("someVariable", "value"))
            .send()
            .join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofSeconds(60));

    assertTrue(SampleDelegate.executed);
    assertFalse(SampleDelegate.canReachExecutionVariable);
    assertEquals("value", SampleDelegate.capturedVariable);
    assertEquals("42", SampleDelegate.capturedBusinessKey);
  }

  @Test
  public void testDelegateExpression() {
    BpmnModelInstance bpmn =
        Bpmn.createExecutableProcess("test")
            .startEvent()
            .serviceTask()
            .zeebeJobType("camunda-7-adapter")
            .zeebeTaskHeader("delegateExpression", "${delegateBean}")
            .endEvent()
            .done();

    zeebeClient.newDeployResourceCommand().addProcessModel(bpmn, "test.bpmn").send().join();

    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("test")
            .latestVersion()
            .variables(Collections.singletonMap("someVariable", "value"))
            .send()
            .join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofSeconds(60));

    assertTrue(SampleDelegateBean.executed);
    assertFalse(SampleDelegateBean.canReachExecutionVariable);
    assertEquals("value", SampleDelegateBean.capturedVariable);
    assertEquals("42", SampleDelegateBean.capturedBusinessKey);
  }

  @Test
  public void testExpression() {
    BpmnModelInstance bpmn =
        Bpmn.createExecutableProcess("test")
            .startEvent()
            .serviceTask()
            .zeebeJobType("camunda-7-adapter")
            .zeebeTaskHeader("expression", "${sampleBean.doStuff(execution,someVariable)}")
            .endEvent()
            .done();

    zeebeClient.newDeployResourceCommand().addProcessModel(bpmn, "test.bpmn").send().join();

    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("test")
            .latestVersion()
            .variables(Collections.singletonMap("someVariable", "value"))
            .send()
            .join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofSeconds(60));

    assertTrue(SampleBean.executionReceived);
    assertTrue(SampleBean.someVariableReceived);
  }

  @Test
  public void testExternalTaskHandlerWrapper() {
    BpmnModelInstance bpmn =
        Bpmn.createExecutableProcess("test")
            .startEvent()
            .serviceTask()
            .zeebeJobType("test-topic")
            .endEvent()
            .done();

    zeebeClient.newDeployResourceCommand().addProcessModel(bpmn, "test.bpmn").send().join();

    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("test")
            .latestVersion()
            .variables(Collections.singletonMap("someVariable", "value"))
            .send()
            .join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofSeconds(60));
    assertEquals("value", SampleExternalTaskHandler.someVariable);
  }
}
