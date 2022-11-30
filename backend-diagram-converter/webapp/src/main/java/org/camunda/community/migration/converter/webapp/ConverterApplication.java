package org.camunda.community.migration.converter.webapp;

import org.camunda.community.migration.converter.BpmnConverter;
import org.camunda.community.migration.converter.BpmnConverterFactory;
import org.camunda.community.migration.converter.NotificationService;
import org.camunda.community.migration.converter.NotificationServiceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConverterApplication {
  public static void main(String[] args) {
    SpringApplication.run(ConverterApplication.class, args);
  }

  @Bean
  public BpmnConverter bpmnConverter(NotificationService notificationService) {
    NotificationServiceFactory.getInstance().setInstance(notificationService);
    return BpmnConverterFactory.getInstance().get();
  }
}
