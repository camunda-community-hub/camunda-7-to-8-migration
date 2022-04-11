package io.berndruecker.converter.example;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

  public static void main(String... args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    ProcessEngine engine = context.getBean(ProcessEngine.class);

    engine.getRuntimeService().startProcessInstanceByKey(
            "sample-process-solution-process",
            Variables.createVariables().putValue("x", 7));
  }

}