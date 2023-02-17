package org.camunda.community.migration.processInstance.configuration;

import io.camunda.zeebe.model.bpmn.instance.StartEvent;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintRule;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintRule.ProcessDefinitionMigrationHintRuleImpl;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintRule;
import org.camunda.community.migration.processInstance.service.ProcessInstanceMigrationHintRule.ProcessInstanceMigrationHintRuleImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MigrationHintRuleConfiguration {

  @Bean
  public ProcessInstanceMigrationHintRule noCallActivities() {
    return new ProcessInstanceMigrationHintRuleImpl(
        "Process instance contains an active call activity. This cannot be migrated",
        pd -> pd.getActivities().stream().anyMatch(a -> a.getType().equals("callActivity")));
  }

  @Bean
  public ProcessInstanceMigrationHintRule noMultiInstances() {
    return new ProcessInstanceMigrationHintRuleImpl(
        "Process instance contains a multi-instance. This cannot be migrated",
        pd -> pd.getActivities().stream().anyMatch(d -> d.getType().equals("multiInstanceBody")));
  }

  @Bean
  public ProcessInstanceMigrationHintRule onlyProcessScopeVariables() {
    return new ProcessInstanceMigrationHintRuleImpl(
        "The process instance contains variables that are not in process scope",
        pd ->
            pd.getProcessVariables().entrySet().stream()
                .anyMatch(e -> !e.getValue().getScope().equals(pd.getProcessInstanceId())));
  }

  @Bean
  public ProcessInstanceMigrationHintRule noTimerEvents() {
    return new ProcessInstanceMigrationHintRuleImpl(
        "The process instance has running timers, they cannot be migrated",
        pd -> pd.getActivities().stream().anyMatch(d -> d.getType().contains("Timer")));
  }

  @Bean
  public ProcessDefinitionMigrationHintRule onlyNoneStartEvents() {
    return new ProcessDefinitionMigrationHintRuleImpl(
        "A process definition must contain a None Start Event",
        data ->
            data.getBpmnModelInstance().getModelElementsByType(StartEvent.class).stream()
                .noneMatch(startEvent -> startEvent.getEventDefinitions().isEmpty()));
  }
}
