package org.camunda.community.migration.processInstance.dto;

import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import org.camunda.community.migration.processInstance.service.ProcessDefinitionMigrationHintRule.ProcessDefinitionMigrationHintRuleContext;

public class Camunda8ProcessDefinitionData implements ProcessDefinitionMigrationHintRuleContext {
  private ProcessDefinition processDefinition;
  private BpmnModelInstance bpmnModelInstance;

  public ProcessDefinition getProcessDefinition() {
    return processDefinition;
  }

  public void setProcessDefinition(ProcessDefinition processDefinition) {
    this.processDefinition = processDefinition;
  }

  public BpmnModelInstance getBpmnModelInstance() {
    return bpmnModelInstance;
  }

  public void setBpmnModelInstance(BpmnModelInstance bpmnModelInstance) {
    this.bpmnModelInstance = bpmnModelInstance;
  }
}
