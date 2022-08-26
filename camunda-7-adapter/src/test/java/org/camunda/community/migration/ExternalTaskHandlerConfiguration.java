package org.camunda.community.migration;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalTaskHandlerConfiguration {

  @Bean
  @ExternalTaskSubscription(topicName = "test-topic")
  public ExternalTaskHandler testHandler() {
    return (externalTask, externalTaskService) -> externalTaskService.complete(externalTask);
  }
}
