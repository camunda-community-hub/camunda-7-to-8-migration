package org.camunda.community.migration.processInstance.configuration;

import java.util.Set;
import java.util.function.Consumer;
import org.camunda.community.migration.processInstance.client.Camunda7Client;
import org.camunda.community.migration.processInstance.client.Camunda7RestClient;
import org.camunda.community.migration.processInstance.properties.Camunda7ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@ConditionalOnMissingBean(Camunda7Client.class)
public class Camunda7ClientConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(Camunda7ClientConfiguration.class);
  private final Camunda7ClientProperties properties;

  @Autowired
  public Camunda7ClientConfiguration(Camunda7ClientProperties properties) {
    this.properties = properties;
  }

  @Bean
  public Camunda7Client camunda7Client(
      RestTemplate restTemplate, Camunda7ClientProperties properties) {
    return new Camunda7RestClient(restTemplate, properties);
  }

  @Bean
  public RestTemplate camunda7HttpClient(Set<Consumer<RestTemplate>> modifiers) {
    RestTemplate restTemplate = new RestTemplate();
    modifiers.forEach(m -> m.accept(restTemplate));
    return restTemplate;
  }

  @Bean
  public Consumer<RestTemplate> baseUrlSetter() {
    String baseUrl =
        (properties.getBaseUrl() + "/" + properties.getRestApiContext())
            .replaceAll("([^:])\\/\\/", "$1/");
    LOG.info("Base URL: {}", baseUrl);
    DefaultUriBuilderFactory templateHandler = new DefaultUriBuilderFactory(baseUrl);
    return restTemplate -> restTemplate.setUriTemplateHandler(templateHandler);
  }

  @Bean
  @ConditionalOnProperty({
    "camunda7.client.authentication.basic-auth.username",
    "camunda7.client.authentication.basic-auth.password"
  })
  public Consumer<RestTemplate> basicAuthSetter() {
    String username = properties.getAuthentication().getBasicAuth().getUsername();
    String password = properties.getAuthentication().getBasicAuth().getPassword();
    LOG.info("Applying Basic Auth");
    return restTemplate ->
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
  }

  @Bean
  @ConditionalOnProperty("camunda7.client.authentication.custom")
  public Consumer<RestTemplate> customAuthSetter(ApplicationContext context) {
    String custom = properties.getAuthentication().getCustom();
    LOG.info("Applying Custom Auth from Interceptor '{}'", custom);
    ClientHttpRequestInterceptor requestInterceptor =
        context.getBean(custom, ClientHttpRequestInterceptor.class);
    return restTemplate -> restTemplate.getInterceptors().add(requestInterceptor);
  }
}
