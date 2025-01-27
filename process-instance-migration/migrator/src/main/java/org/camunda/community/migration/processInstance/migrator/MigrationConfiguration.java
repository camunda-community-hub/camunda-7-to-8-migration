package org.camunda.community.migration.processInstance.migrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.community.migration.processInstance.api.ProcessInstanceDataExporter;
import org.camunda.community.migration.processInstance.exporter.Camunda7Exporter;
import org.camunda.community.migration.processInstance.exporter.EmbeddedProcessEngineService;
import org.camunda.community.migration.processInstance.exporter.api.ProcessEngineService;
import org.camunda.community.migration.processInstance.importer.Camunda8ProcessInstanceImporter;
import org.camunda.community.migration.processInstance.importer.ProcessDefinitionMappingRules;
import org.camunda.community.migration.processInstance.importer.impl.Camunda8ProcessInstanceImporterImpl;
import org.camunda.community.migration.processInstance.importer.impl.ProcessDefinitionMappingRulesImpl;
import org.camunda.community.migration.processInstance.migrator.impl.InMemoryProcessInstanceRepository;
import org.camunda.community.migration.processInstance.migrator.impl.ProcessInstanceManager;
import org.camunda.community.migration.processInstance.migrator.impl.ProcessInstanceMigrationExecutor;
import org.camunda.community.migration.processInstance.migrator.impl.ProcessInstanceMigratorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MigrationConfiguration {

  @Bean
  public ProcessInstanceMigrationExecutor processInstanceMigrationExecutor(
      ScheduledExecutorService extractExecutorService,
      ScheduledExecutorService startExecutorService,
      ProcessInstanceMigrationSteps migrationSteps) {
    // TODO make rate configurable with properties
    return new ProcessInstanceMigrationExecutor(
        extractExecutorService, startExecutorService, migrationSteps, Duration.ofSeconds(5));
  }

  @Bean
  public ScheduledExecutorService extractExecutorService() {
    return Executors.newSingleThreadScheduledExecutor();
  }

  @Bean
  public ScheduledExecutorService startExecutorService() {
    return Executors.newSingleThreadScheduledExecutor();
  }

  @Bean
  public ProcessInstanceMigratorImpl migrator(
      ProcessInstanceDataExporter processInstanceDataExporter,
      Camunda8ProcessInstanceImporter processInstanceImporter,
      ProcessInstanceManager processInstanceManager) {
    return new ProcessInstanceMigratorImpl(
        processInstanceDataExporter, processInstanceImporter, processInstanceManager);
  }

  @Bean
  public ProcessInstanceDataExporter processInstanceDataExporter(
      ProcessEngineService processEngineService) {
    return new Camunda7Exporter(processEngineService);
  }

  @Bean
  public ProcessEngineService processEngineService(
      ProcessEngine processEngine, ObjectMapper objectMapper) {
    return new EmbeddedProcessEngineService(processEngine, objectMapper);
  }

  @Bean
  public Camunda8ProcessInstanceImporter processInstanceImporter(
      ZeebeClient zeebeClient, ProcessDefinitionMappingRules processDefinitionMappingRules) {
    return new Camunda8ProcessInstanceImporterImpl(zeebeClient, processDefinitionMappingRules);
  }

  @Bean
  public ProcessDefinitionMappingRules processDefinitionMappingRules() {
    // TODO make this map configurable from properties
    return new ProcessDefinitionMappingRulesImpl(Map.of());
  }

  @Bean
  public ProcessInstanceManager processInstanceManager(
      ProcessInstanceRepository processInstanceRepository) {
    return new ProcessInstanceManager(processInstanceRepository);
  }

  @Bean
  public ProcessInstanceRepository processInstanceRepository() {
    return new InMemoryProcessInstanceRepository();
  }
}
