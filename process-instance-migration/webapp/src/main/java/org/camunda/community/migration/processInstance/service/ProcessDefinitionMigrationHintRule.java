package org.camunda.community.migration.processInstance.service;

import io.camunda.operate.model.ProcessDefinition;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.util.Optional;
import java.util.function.Predicate;

public interface ProcessDefinitionMigrationHintRule {
  Optional<String> createHint(ProcessDefinitionMigrationHintRuleContext data);

  interface ProcessDefinitionMigrationHintRuleContext {
    public ProcessDefinition getProcessDefinition();

    public BpmnModelInstance getBpmnModelInstance();
  }

  class ProcessDefinitionMigrationHintRuleImpl implements ProcessDefinitionMigrationHintRule {
    private final String hint;
    private final Predicate<ProcessDefinitionMigrationHintRuleContext> condition;

    public ProcessDefinitionMigrationHintRuleImpl(
        String hint, Predicate<ProcessDefinitionMigrationHintRuleContext> condition) {
      this.hint = hint;
      this.condition = condition;
    }

    @Override
    public Optional<String> createHint(ProcessDefinitionMigrationHintRuleContext data) {
      if (condition.test(data)) {
        return Optional.of(hint);
      }
      return Optional.empty();
    }
  }
}
