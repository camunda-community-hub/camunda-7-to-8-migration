package org.camunda.community.migration.processInstance.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ActivityData;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData.ProcessVariableData;

public interface ProcessInstanceMigrationHintRule {
  Optional<String> createHint(ProcessInstanceMigrationHintRuleContext context);

  interface ProcessInstanceMigrationHintRuleContext {
    public String getProcessInstanceId();

    public String getProcessDefinitionKey();

    public Map<String, ProcessVariableData> getProcessVariables();

    public List<ActivityData> getActivities();
  }

  class ProcessInstanceMigrationHintRuleImpl implements ProcessInstanceMigrationHintRule {
    private final String hint;
    private final Predicate<ProcessInstanceMigrationHintRuleContext> condition;

    public ProcessInstanceMigrationHintRuleImpl(
        String hint, Predicate<ProcessInstanceMigrationHintRuleContext> condition) {
      this.hint = hint;
      this.condition = condition;
    }

    @Override
    public Optional<String> createHint(ProcessInstanceMigrationHintRuleContext processData) {
      if (condition.test(processData)) {
        return Optional.of(hint);
      }
      return Optional.empty();
    }
  }
}
