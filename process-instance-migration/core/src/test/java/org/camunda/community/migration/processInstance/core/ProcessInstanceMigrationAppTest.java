package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.camunda.community.migration.processInstance.core.dto.Camunda7Version;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ZeebeSpringTest
public class ProcessInstanceMigrationAppTest {

  @Autowired ProcessInstanceMigrationStarter processInstanceMigrationStarter;
  @Autowired Camunda7Client camunda7Client;

  @Test
  @Disabled
  void shouldRunProcess() {
    ProcessInstanceEvent processInstance =
        processInstanceMigrationStarter.startProcessInstanceMigration("12345");
    waitForProcessInstanceCompleted(processInstance);
  }

  @Test
  void shouldConnectToCamunda7() {
    Camunda7Version version = camunda7Client.getVersion();
    assertThat(version.getVersion()).isEqualTo("7.18.0");
  }
}
