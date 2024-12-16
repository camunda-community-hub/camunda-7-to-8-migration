package org.camunda.community.migration_example;

import io.camunda.process.test.api.CamundaAssert;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.community.migration_example.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

@SpringBootTest
@CamundaSpringProcessTest
public class ApplicationTest {

  @Autowired
  ZeebeClient zeebeClient;

  @MockBean
  CustomerService mockedCustomerService;

  @Test
  void shouldRunProcess_HappyPath() throws InterruptedException, TimeoutException {

    when(mockedCustomerService.getCustomerCredit("cust50")).thenReturn(50.0);
    when(mockedCustomerService.deductCredit("cust50", 40.0)).thenReturn(0.0);

    ProcessInstanceEvent processInstance = zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId("PaymentProcess")
        .latestVersion()
        .variables(Variables
            .createVariables()
            .putValue("orderTotal", 40.0)
            .putValue("customerId", "cust50"))
        .send()
        .join();

    CamundaAssert
        .assertThat(processInstance)
        .isCompleted()
        .hasCompletedElements("Payment completed");
    //                                             .hasNotPassedElement("Activity_0rwd82t");
    // missing hasNotCompleted()
  }

}
