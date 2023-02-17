package org.camunda.community.migration.processInstance.configuration;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.auth.AuthInterface;
import io.camunda.operate.auth.SaasAuthentication;
import io.camunda.operate.auth.SelfManagedAuthentication;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.exception.OperateException;
import org.camunda.community.migration.processInstance.properties.OperateClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperateClientConfiguration {
  private final OperateClientProperties properties;

  @Autowired
  public OperateClientConfiguration(OperateClientProperties properties) {
    this.properties = properties;
  }

  @Bean
  public AuthInterface operateAuthentication() {
    if (properties.getAuthentication().getSaas() != null) {
      return new SaasAuthentication(
          properties.getAuthentication().getSaas().getClientId(),
          properties.getAuthentication().getSaas().getClientSecret());
    }
    if (properties.getAuthentication().getSelfManaged() != null) {
      return new SelfManagedAuthentication()
          .clientId(properties.getAuthentication().getSelfManaged().getClientId())
          .clientSecret(properties.getAuthentication().getSelfManaged().getClientSecret())
          .keycloakUrl(properties.getAuthentication().getSelfManaged().getKeycloakUrl())
          .keycloakRealm(properties.getAuthentication().getSelfManaged().getKeycloakRealm());
    }
    if (properties.getAuthentication().getSimple() != null) {
      return new SimpleAuthentication(
          properties.getAuthentication().getSimple().getUsername(),
          properties.getAuthentication().getSimple().getPassword(),
          properties.getBaseUrl());
    }
    throw new IllegalStateException("No authentication configured for Operate client");
  }

  @Bean
  public CamundaOperateClient operateClient(AuthInterface authentication) throws OperateException {
    return new CamundaOperateClient.Builder()
        .operateUrl(properties.getBaseUrl())
        .authentication(authentication)
        .build();
  }
}
