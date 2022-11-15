package org.camunda.community.converter.webapp;

import java.util.Set;
import org.camunda.community.converter.webapp.properties.ProcessEngineClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProcessEngineClientConfiguration {
  private final ProcessEngineClientProperties properties;

  @Autowired
  public ProcessEngineClientConfiguration(ProcessEngineClientProperties properties) {
    this.properties = properties;
  }

  @Bean
  @Qualifier("camunda-engine-client")
  public RestTemplate processEngineClient(Set<ClientHttpRequestInterceptor> interceptors) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().addAll(interceptors);
    return restTemplate;
  }

  @Bean
  public ClientHttpRequestInterceptor basicAuthInterceptor() {
    if (properties.getSecurity().getBasicAuth().isEnabled()) {
      return new BasicAuthenticationInterceptor(
          properties.getSecurity().getBasicAuth().getUsername(),
          properties.getSecurity().getBasicAuth().getPassword());
    } else {
      return null;
    }
  }
}
