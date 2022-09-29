package org.camunda.community.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConverterApplication {
  public static void main(String[] args) {
    SpringApplication.run(ConverterApplication.class, args);
  }

  @Bean
  public BpmnConverter bpmnConverter() {
    return new BpmnConverter(
        DomElementVisitorFactory.getInstance().get(), ConversionFactory.getInstance().get());
  }
}
