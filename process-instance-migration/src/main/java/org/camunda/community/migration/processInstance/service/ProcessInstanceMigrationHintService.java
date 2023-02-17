package org.camunda.community.migration.processInstance.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.Camunda7ProcessInstanceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessInstanceMigrationHintService {
  private final Set<ProcessInstanceMigrationHintRule> processInstanceMigrationHintRules;

  @Autowired
  public ProcessInstanceMigrationHintService(
      Set<ProcessInstanceMigrationHintRule> processInstanceMigrationHintRules) {
    this.processInstanceMigrationHintRules = processInstanceMigrationHintRules;
  }

  public List<String> getMigrationHints(Camunda7ProcessInstanceData processData) {
    return processInstanceMigrationHintRules.stream()
        .map(rule -> rule.createHint(processData))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private <T> Optional<String> createHint(List<T> data, Predicate<T> condition, String hint) {
    return data.stream().filter(condition).findAny().map(ad -> hint);
  }
}
