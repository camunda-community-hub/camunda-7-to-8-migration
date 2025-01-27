package org.camunda.community.migration.processInstance.importer;

import java.util.Optional;

public interface ProcessDefinitionMappingRules {
  Optional<MappedProcessDefinition> checkProcessDefinition(String processDefinitionKey);

  record MappedProcessDefinition(Long processDefinitionKey) {}
}
