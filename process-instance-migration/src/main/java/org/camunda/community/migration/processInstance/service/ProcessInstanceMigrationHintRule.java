package org.camunda.community.migration.processInstance.service;

import java.util.Optional;
import java.util.function.Predicate;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;

public interface ProcessInstanceMigrationHintRule {
  Optional<String> createHint(Camunda7ProcessInstanceData processData);

  class ProcessInstanceMigrationHintRuleImpl implements ProcessInstanceMigrationHintRule {
    private final String hint;
    private final Predicate<Camunda7ProcessInstanceData> condition;

    public ProcessInstanceMigrationHintRuleImpl(
        String hint, Predicate<Camunda7ProcessInstanceData> condition) {
      this.hint = hint;
      this.condition = condition;
    }

    @Override
    public Optional<String> createHint(Camunda7ProcessInstanceData processData) {
      if (condition.test(processData)) {
        return Optional.of(hint);
      }
      return Optional.empty();
    }
  }
}
