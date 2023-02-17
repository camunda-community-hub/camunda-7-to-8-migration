package org.camunda.community.migration.processInstance;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.assertj.core.api.Assertions.*;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.Process;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;

public class TestUtil {
  static ZeebeClient zeebeClient;
  static ZeebeTestEngine zeebeTestEngine;

  public static void completeUserTask(Object variables) {
    ActivateJobsResponse response =
        zeebeClient
            .newActivateJobsCommand()
            .jobType(USER_TASK_JOB_TYPE)
            .maxJobsToActivate(1)
            .send()
            .join();
    assertThat(response.getJobs()).isNotEmpty().hasSize(1);
    ActivatedJob userTask = response.getJobs().get(0);
    zeebeClient.newCompleteCommand(userTask).variables(variables).send().join();
  }

  public static Process deployProcessToZeebe(String resourceName) {
    return zeebeClient
        .newDeployResourceCommand()
        .addResourceFromClasspath(resourceName)
        .send()
        .join()
        .getProcesses()
        .stream()
        .findFirst()
        .get();
  }

  public static Process deployProcessToZeebe(BpmnModelInstance modelInstance) {
    return zeebeClient
        .newDeployResourceCommand()
        .addProcessModel(modelInstance, "test-model.bpmn")
        .send()
        .join()
        .getProcesses()
        .stream()
        .findFirst()
        .get();
  }

  public static void deployCamunda7Process(String resourceName) {
    ProcessEngines.getDefaultProcessEngine()
        .getRepositoryService()
        .createDeployment()
        .addClasspathResource(resourceName)
        .deploy();
  }

  public static void deleteDeployments() {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    processEngine
        .getRepositoryService()
        .createDeploymentQuery()
        .list()
        .forEach(
            deployment ->
                processEngine
                    .getRepositoryService()
                    .deleteDeployment(deployment.getId(), true, true, true));
  }
}
