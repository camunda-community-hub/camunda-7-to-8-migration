package org.camunda.community.migration.processInstance;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources = "classpath*:*.bpmn")
@EnableZeebeClient
public class ProcessInstanceMigrationApp {
  public static void main(String[] args) {
    SpringApplication.run(ProcessInstanceMigrationApp.class, args);
  }
}
