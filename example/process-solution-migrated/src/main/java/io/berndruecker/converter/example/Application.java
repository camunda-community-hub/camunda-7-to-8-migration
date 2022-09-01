package io.berndruecker.converter.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import org.camunda.community.migration.EnableCamunda7Adapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;

@SpringBootApplication
@EnableZeebeClient
@EnableCamunda7Adapter
@ZeebeDeployment(resources = "classpath:*.bpmn")
public class Application {

  public static void main(String... args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);


    ZeebeClient engineClient = context.getBean(ZeebeClient.class);
    engineClient.newCreateInstanceCommand().bpmnProcessId("sample-process-solution-process").latestVersion().variables(Collections.singletonMap("x", 7)).send().join();
  }

}