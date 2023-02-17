package org.camunda.community.migration.processInstance.service;

import java.util.Optional;
import java.util.function.Predicate;
import org.camunda.community.migration.processInstance.dto.Camunda8ProcessDefinitionData;

public interface ProcessDefinitionMigrationHintRule {
  Optional<String> createHint(Camunda8ProcessDefinitionData data);

  class ProcessDefinitionMigrationHintRuleImpl implements ProcessDefinitionMigrationHintRule {
    private final String hint;
    private final Predicate<Camunda8ProcessDefinitionData> condition;

    public ProcessDefinitionMigrationHintRuleImpl(
        String hint, Predicate<Camunda8ProcessDefinitionData> condition) {
      this.hint = hint;
      this.condition = condition;
    }

    @Override
    public Optional<String> createHint(Camunda8ProcessDefinitionData data) {
      if (condition.test(data)) {
        return Optional.of(hint);
      }
      return Optional.empty();
    }
  }
}
