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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = CamundaPlatform7AdapterConfig.class,
    properties = "logging.level.root=INFO")
@ZeebeSpringTest
public class CamundaPlatform7AdapterTest {

  @Autowired private ZeebeClient zeebeClient;

  @BeforeEach
  public void setup() {
    SampleBean.executionReceived = false;
    SampleBean.someVariableReceived = false;
    SampleDelegate.canReachExecutionVariable = false;
    SampleDelegate.capturedBusinessKey = null;
    SampleDelegate.capturedVariable = null;
    SampleDelegate.executed = false;
    SampleDelegateBean.canReachExecutionVariable = false;
    SampleDelegateBean.capturedBusinessKey = null;
    SampleDelegateBean.capturedVariable = null;
    SampleDelegateBean.executed = false;
  }

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
    VariableDto variableValue = new VariableDto();
    variableValue.setValue("value");
    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("test")
            .latestVersion()
            .variables(Collections.singletonMap("someVariable", variableValue))
            .send()
            .join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofSeconds(60));

    assertTrue(SampleDelegate.executed);
    assertFalse(SampleDelegate.canReachExecutionVariable);
    assertNotNull(SampleDelegate.capturedVariable);
    assertEquals("42", SampleDelegate.capturedBusinessKey);
  }

  @Test
  public void testDelegateExpression() {
    BpmnModelInstance bpmn =
        Bpmn.createExecutableProcess("test2")
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
            .bpmnProcessId("test2")
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
        Bpmn.createExecutableProcess("test2")
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
            .bpmnProcessId("test2")
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
        Bpmn.createExecutableProcess("test2")
            .startEvent()
            .serviceTask()
            .zeebeJobType("test-topic")
            .endEvent()
            .done();

    zeebeClient.newDeployResourceCommand().addProcessModel(bpmn, "test.bpmn").send().join();

    ProcessInstanceEvent processInstance =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("test2")
            .latestVersion()
            .variables(Collections.singletonMap("someVariable", "value"))
            .send()
            .join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofSeconds(60));
    assertEquals("value", SampleExternalTaskHandler.someVariable);
  }

  @Test
  void testBpmnError() {
    zeebeClient
        .newDeployResourceCommand()
        .addResourceFromClasspath("test-with-error-event.bpmn")
        .send()
        .join();
    ProcessInstanceEvent join =
        zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("error-test")
            .latestVersion()
            .send()
            .join();
    waitForProcessInstanceCompleted(join, Duration.ofSeconds(60));
    assertTrue(SampleDelegateBean.executed);
  }
}
