package org.camunda.community.migration.processInstance.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.camunda.community.migration.processInstance.dto.Camunda8ProcessDefinitionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessDefinitionMigrationHintService {
  private final Set<ProcessDefinitionMigrationHintRule> rules;

  @Autowired
  public ProcessDefinitionMigrationHintService(Set<ProcessDefinitionMigrationHintRule> rules) {
    this.rules = rules;
  }

  public List<String> getMigrationHints(Camunda8ProcessDefinitionData data) {
    return rules.stream()
        .map(rule -> rule.createHint(data))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }
}
