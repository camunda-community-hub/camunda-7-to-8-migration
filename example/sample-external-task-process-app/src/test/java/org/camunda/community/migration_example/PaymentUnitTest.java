package org.camunda.community.migration_example;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.community.process_test_coverage.junit5.platform7.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Deployment(resources = { "payment_process.bpmn" })
@ExtendWith(ProcessEngineCoverageExtension.class)
public class PaymentUnitTest {

  @Test
  public void happy_path_test() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PaymentProcess",
        withVariables("orderTotal", 45.99, "customerCredit", 30.00));
    assertThat(processInstance).isWaitingAt(findId("Deduct customer credit")).externalTask()
        .hasTopicName("creditDeduction");
    complete(externalTask());
    assertThat(processInstance).isWaitingAt(findId("Charge credit card")).externalTask()
        .hasTopicName("creditCardCharging");
    complete(externalTask());
    assertThat(processInstance).isEnded().hasPassed(findId("Payment completed"));
  }

  @Test
  public void credit_card_failure_test() {
    ProcessInstance processInstance = runtimeService().createProcessInstanceByKey("PaymentProcess")
        .startBeforeActivity(findId("Charge credit card")).execute();
    assertThat(processInstance).isWaitingAt(findId("Charge credit card"));

    fetchAndLock("creditCardCharging", "junit-test-worker", 1);
    externalTaskService().handleBpmnError(externalTask().getId(), "junit-test-worker", "creditCardChargeError");
    
    assertThat(processInstance).isWaitingAt(findId("Check failed payment data"));
    
    complete(task(), withVariables("errorResolved", false));

    assertThat(processInstance).isEnded().hasPassed(findId("Payment failed"));
  }

}
