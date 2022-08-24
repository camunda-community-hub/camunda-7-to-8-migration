package org.camunda.community.migration;

import org.camunda.bpm.engine.ArtifactFactory;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.ProcessEngineElContext;
import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ExpressionFactory;
import org.camunda.bpm.engine.impl.javax.el.FunctionMapper;
import org.camunda.bpm.engine.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.engine.spring.SpringArtifactFactory;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Set;

@Configuration
@ComponentScan("org.camunda.community.migration")
public class CamundaPlatform7AdapterConfig {

  @Bean
  @ConditionalOnMissingBean
  public ExpressionFactory expressionFactory() {
    return new ExpressionFactoryImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ELContext elContext(Set<FunctionMapper> functionMappers) {
    return new ProcessEngineElContext(new ArrayList<>(functionMappers));
  }

  @Bean
  @ConditionalOnMissingBean
  public ExpressionManager expressionManager(ApplicationContext applicationContext) {
    return new SpringExpressionManager(applicationContext);
  }

  @Bean
  @ConditionalOnMissingBean
  public ArtifactFactory artifactFactory(ApplicationContext applicationContext) {
    return new SpringArtifactFactory(applicationContext);
  }
}
