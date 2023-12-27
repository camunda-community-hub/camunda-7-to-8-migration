package org.camunda.community.migration_example;

import org.camunda.community.migration.adapter.EnableCamunda7Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@EnableCamunda7Adapter
@Deployment(resources = "classpath*:*.bpmn")
public class ExternalTaskWorkerApplication {
  
  private static final Logger LOG = LoggerFactory.getLogger(ExternalTaskWorkerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ExternalTaskWorkerApplication.class, args);
  }
  
}
