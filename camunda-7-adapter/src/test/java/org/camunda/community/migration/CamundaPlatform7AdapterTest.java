package org.camunda.community.migration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CamundaPlatform7AdapterConfig.class)
@ZeebeSpringTest
public class CamundaPlatform7AdapterTest {

    @Autowired
    private ZeebeClient zeebeClient;

    @Test
    public void testSomething() {
        BpmnModelInstance bpmn = Bpmn.createExecutableProcess("test")
                .startEvent()
                .serviceTask()
                    .zeebeJobType("camunda-7-adapter")
                    .zeebeTaskHeader("class", "org.camunda.community.migration.SampleDelegate")
                .endEvent().done();

        zeebeClient.newDeployResourceCommand()
                .addProcessModel(bpmn, "test.bpmn")
                .send().join();

        ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("test").latestVersion()
                .variables(Collections.singletonMap("someVariable", "value"))
                .send().join();

        waitForProcessInstanceCompleted(processInstance);

        assertTrue(SampleDelegate.executed);
        assertEquals("value", SampleDelegate.capturedVariable);
        assertEquals("42", SampleDelegate.capturedBusinessKey);
    }
}
