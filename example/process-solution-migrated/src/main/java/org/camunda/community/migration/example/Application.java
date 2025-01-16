package org.camunda.community.migration.example;

import io.camunda.client.CamundaClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import java.util.Collections;
import org.camunda.community.migration.adapter.EnableCamunda7Adapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCamunda7Adapter
@Deployment(resources = "classpath*:*.bpmn")
public class Application {

  public static void main(String... args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    CamundaClient engineClient = context.getBean(CamundaClient.class);
    engineClient
        .newCreateInstanceCommand()
        .bpmnProcessId("sample-process-solution-process")
        .latestVersion()
        .variables(Collections.singletonMap("x", 7))
        .send()
        .join();
  }
}
