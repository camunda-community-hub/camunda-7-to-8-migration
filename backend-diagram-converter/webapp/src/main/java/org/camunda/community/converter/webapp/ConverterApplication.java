package org.camunda.community.converter.webapp;

import org.camunda.community.converter.BpmnConverter;
import org.camunda.community.converter.BpmnConverterFactory;
import org.camunda.community.converter.NotificationService;
import org.camunda.community.converter.NotificationServiceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConverterApplication {
  public static void main(String[] args) {
    ConfigurableApplicationContext context =
        SpringApplication.run(ConverterApplication.class, args);
    NotificationServiceFactory.getInstance()
        .setInstance(context.getBean(NotificationService.class));
  }

  @Bean
  public BpmnConverter bpmnConverter() {
    return BpmnConverterFactory.getInstance().get();
  }
}
