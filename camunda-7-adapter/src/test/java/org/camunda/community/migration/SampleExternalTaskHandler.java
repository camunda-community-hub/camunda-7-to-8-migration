package org.camunda.community.migration;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@ExternalTaskSubscription(topicName = "test-topic")
@Component
public class SampleExternalTaskHandler implements ExternalTaskHandler {
  public static String someVariable;
  public static boolean executed = false;

  @Override
  public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    someVariable = externalTask.getVariable("someVariable");
    executed = true;
    externalTaskService.complete(externalTask);
  }
}
