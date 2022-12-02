package org.camunda.community.migration.processInstance.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Set;
import java.util.function.Consumer;

@Configuration
public class Camunda7ClientConfiguration {

  @Bean
  public RestTemplate camunda7HttpClient(Set<Consumer<RestTemplate>> modifiers) {
    RestTemplate restTemplate = new RestTemplate();
    modifiers.forEach(m -> m.accept(restTemplate));
    return restTemplate;
  }

  @Bean
  public Consumer<RestTemplate> baseUrlSetter(Camunda7ClientProperties camunda7ClientProperties) {
    DefaultUriBuilderFactory templateHandler =
        new DefaultUriBuilderFactory(camunda7ClientProperties.getBaseUrl());
    return restTemplate -> restTemplate.setUriTemplateHandler(templateHandler);
  }
}
