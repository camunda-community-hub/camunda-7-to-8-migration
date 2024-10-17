package org.camunda.community.migration_example;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.community.migration_example.services.CustomerService;
import org.camunda.community.process_test_coverage.spring_test.platform8.ProcessEngineCoverageConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;

@SpringBootTest
@ZeebeSpringTest
@Import(ProcessEngineCoverageConfiguration.class)
@Disabled
public class ApplicationTest {

  @Autowired
  ZeebeClient zeebeClient;

  @Autowired
  ZeebeTestEngine zeebeTestEngine;

  @MockBean
  CustomerService mockedCustomerService;

  @Test
  void shouldRunProcess_HappyPath() throws InterruptedException, TimeoutException {

    when(mockedCustomerService.getCustomerCredit("cust50")).thenReturn(50.0);
    when(mockedCustomerService.deductCredit("cust50", 40.0)).thenReturn(0.0);

    ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
        .bpmnProcessId("PaymentProcess").latestVersion().variables(Variables.createVariables()
            .putValue("orderTotal", 40.0).putValue("customerId", "cust50"))
        .send().join();

    waitForProcessInstanceCompleted(processInstance, Duration.ofMinutes(1));
    assertThat(processInstance).isCompleted().hasPassedElement("Event_1xh41u2")
        .hasNotPassedElement("Activity_0rwd82t");
  }

}
