package org.camunda.community.migration.automatictest;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class AutomaticTestApp {

  // service/ExecuteTest.init()
  public static void main(String[] args) {
    SpringApplication.run(AutomaticTestApp.class, args);
  }
}

