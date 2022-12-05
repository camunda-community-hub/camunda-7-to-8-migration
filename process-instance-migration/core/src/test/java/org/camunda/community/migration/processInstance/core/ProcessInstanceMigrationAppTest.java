package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.camunda.community.migration.processInstance.core.dto.Camunda7Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.*;
import static org.camunda.community.migration.processInstance.core.TestUtil.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ZeebeSpringTest
public class ProcessInstanceMigrationAppTest {

  @Autowired ProcessInstanceMigrationStarter processInstanceMigrationStarter;
  @Autowired Camunda7Client camunda7Client;

  @Autowired ZeebeTestEngine zeebeTestEngine;

  @Test
  @Disabled
  void shouldRunProcess() throws InterruptedException, TimeoutException {
    ProcessInstanceEvent processInstance =
        processInstanceMigrationStarter.startProcessInstanceMigration("12345");
    waitForProcessInstanceHasPassedElement(processInstance, "SuspendProcessDefinitionTask");
    zeebeTestEngine.waitForIdleState(Duration.ofSeconds(10));
    completeUserTask(new HashMap<>());
    waitForProcessInstanceCompleted(processInstance);
  }

  @Test
  void shouldConnectToCamunda7() {
    Camunda7Version version = camunda7Client.getVersion();
    assertThat(version.getVersion()).isEqualTo("7.18.0");
  }
}
