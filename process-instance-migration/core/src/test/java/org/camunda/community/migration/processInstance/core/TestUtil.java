package org.camunda.community.migration.processInstance.core;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import static io.camunda.zeebe.protocol.Protocol.*;
import static org.assertj.core.api.Assertions.*;

public class TestUtil {
  static ZeebeClient zeebeClient;

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
}
