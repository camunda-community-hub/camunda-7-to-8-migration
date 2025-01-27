package org.camunda.community.migration.processInstance.importer.impl;

import java.util.Map;
import java.util.Optional;
import org.camunda.community.migration.processInstance.importer.ProcessDefinitionMappingRules;

public class ProcessDefinitionMappingRulesImpl implements ProcessDefinitionMappingRules {
  private final Map<String, Long> mappedProcessDefinitions;

  public ProcessDefinitionMappingRulesImpl(Map<String, Long> mappedProcessDefinitions) {
    this.mappedProcessDefinitions = mappedProcessDefinitions;
  }

  @Override
  public Optional<MappedProcessDefinition> checkProcessDefinition(String processDefinitionKey) {
    return Optional.ofNullable(mappedProcessDefinitions.get(processDefinitionKey))
        .map(MappedProcessDefinition::new);
  }
}
