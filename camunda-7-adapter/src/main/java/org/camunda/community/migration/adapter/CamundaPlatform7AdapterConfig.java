package org.camunda.community.migration.adapter;

import org.camunda.bpm.client.spring.annotation.EnableExternalTaskClient;
import org.camunda.bpm.client.spring.impl.client.ClientConfiguration;
import org.camunda.bpm.engine.ArtifactFactory;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ExpressionFactory;
import org.camunda.bpm.engine.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.engine.impl.juel.SimpleContext;
import org.camunda.bpm.engine.spring.SpringArtifactFactory;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.camunda.community.migration")
@EnableExternalTaskClient(baseUrl = "http://localhost", disableAutoFetching = true)
public class CamundaPlatform7AdapterConfig {

  @Bean
  public ExternalTaskWorkerRegistration workerRegistration() {
    ClientConfiguration configuration = new ClientConfiguration();
    configuration.setBaseUrl("http://localhost");

    return new ExternalTaskWorkerRegistration(configuration);
  }

  @Bean
  @ConditionalOnMissingBean
  public ExpressionFactory expressionFactory() {
    return new ExpressionFactoryImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ELContext elContext() {
    return new SimpleContext();
  }

  @Bean
  @ConditionalOnMissingBean
  public JuelExpressionManager expressionManager(ApplicationContext applicationContext) {
    return new SpringExpressionManager(applicationContext);
  }

  @Bean
  @ConditionalOnMissingBean
  public ArtifactFactory artifactFactory(ApplicationContext applicationContext) {
    return new SpringArtifactFactory(applicationContext);
  }
}
