package org.camunda.community.migration.processInstance.core;

import static org.camunda.community.migration.processInstance.core.ProcessConstants.Message.*;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.camunda.community.migration.processInstance.core.variables.ProcessInstanceMigrationVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceMigrationService {
  private final ZeebeClient zeebeClient;

  @Autowired
  public ProcessInstanceMigrationService(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  public PublishMessageResponse startProcessInstanceMigration(String bpmnProcessId) {
    ProcessInstanceMigrationVariables variables = new ProcessInstanceMigrationVariables();
    variables.setBpmnProcessId(bpmnProcessId);
    return zeebeClient
        .newPublishMessageCommand()
        .messageName(START)
        .correlationKey(bpmnProcessId)
        .timeToLive(Duration.ofSeconds(3))
        .variables(variables)
        .send()
        .join();
  }

  public void selectProcessInstances(long jobKey, List<String> processInstances) {
    zeebeClient
        .newCompleteCommand(jobKey)
        .variables(Collections.singletonMap("camunda7ProcessInstanceIds", processInstances))
        .send()
        .join();
  }
}
